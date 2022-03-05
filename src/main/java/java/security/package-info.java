/**
 * Classes and interfaces for the Babe security (permissions) framework.
 *
 * TODO:
 *   - filePolicyLoader has no impl
 *   - filePermissions impl
 *
 *   https://docs.oracle.com/javase/6/docs/technotes/guides/security/permissions.html
 *
 * <h2>Design Rationale</h2>
 *
 * <p>
 * The Java permission framework is well entrenched into Java, having been in
 * its first form with Java's first publicly release. Subsequent versions of
 * Java added to, and re-engineered parts of it, adding such classes as the
 * AccessController (for more flexible permissions access control) and the
 * DomainCombiner to facilitate JAAS.
 *
 * <p>
 * The J2SE security framework is large and adds considerably to the size of the
 * 'java' runtime package. In fact, it was considered too large for inclusion in
 * embedded Java (J2ME).
 *
 * <p>
 * The J2ME security framework is not at all like the J2SE one. It is not even
 * modelled on it. It may be small and fast, but it is also quite limited, not
 * extensible, and actually provides no means to <i>forbid</i> access to a
 * resource - it just prompts the user. Not exactly *high* security.
 *
 * <p>
 * Unlike the J2SE permission framework, the J2ME permissions framework is
 * <i>not</i> extensible and does not permit arbitrary types of access to
 * protected resources.
 *
 * <p>
 * The Babe permissions framework is essentially a revisiting of the
 * architecture of the J2SE permissions framework with the goal of making it
 * much smaller, faster, and with less GC, and thus better suited to an embedded
 * environment. Not all features of the J2SE framework are supported, but in the
 * target environment they do not have to be. Legacy aspects of the J2SE design
 * have also been discarded.
 *
 * <p>
 * The basic philosophy is "resources can be protected by requiring running code to be granted permissions to do so"
 * . How this is achieved is by using cut-down, or simplified/modified versions
 * of the J2SE permission classes and interfaces.
 *
 * <p>
 * Compatibility with the J2SE is not a design goal here, but the Babe VM
 * security framework is modelled on the J2SE security framework albeit with a
 * number of key differences:
 *
 * <ul>
 * <li>JAAS support is not included, namely, the J2SE
 * <code>DomainCombiner</code> interface is not supported.
 * <li>The roles of the J2SE AccessController and
 * {@link java.security.SecurityManager} have been combined into a simpler
 * SecurityManager class.
 * <li>The {@link java.security.SecurityManager} is not part of the
 * <code>java.lang</code> package but is part of <code>java.security</code>
 * package to allow it package level access to <code>java.security</code>-only
 * code.
 * <li>The <code>SecurityManager</code> and <code>Policy</code> are not
 * instances, but are fully-static classes.
 * <li>The system security policy as represented by {@link java.security.Policy}
 * is not replaceable. However, the <code>Policy</code> uses a
 * {@link java.security.PolicyLoader} to load and refresh the system security
 * policy.
 * <li>{@link java.security.BasicPermission} is concrete and may have actions.
 * <li>The security framework focuses on <i>access control</i>, not cryptography
 * (yet).
 * </ul>
 *
 * <h2>Overview</h2>
 *
 * <p>
 * As with J2SE, the {@link java.security.SecurityManager} is the hub of access
 * control. J2SE also defines an <code>AccessController</code> that it delegates
 * access control to, but here, the duties of the AccessController have been
 * rolled into the SecurityManager.
 *
 * <p>
 * Unlike J2SE, the SecurityManager has only static methods - there is not
 * getting and setting of the security manager or checking for an instance
 * before performing a permission check. For example, to perform (say) a
 * permission check on a system property call "bob" the following code is used:
 *
 * <pre>
 * SecurityManager.checkPermission(new PropertyPermission(&quot;bob&quot;));
 * </pre>
 *
 * <p>
 * no checking for an installed SecurityManager.
 *
 * <h2>The security Policy</h2>
 *
 * The system security policy is held by the {@link java.security.Policy} class.
 * The Policy class is java.security package-private. The system security policy
 * held by the Policy class is actually defined by an arbitrary number of
 * {@link java.security.PolicyEntry} objects. A PolicyEntry if a definition of
 * permissions for a given CodeSource. The Policy class delegates the creation
 * of the system security policy to a {@link java.security.PolicyLoader}
 * implementation. The PolicyLoader is responsible for (by whatever means it
 * chooses) to provide the Policy with all the PolicyEntry objects that define
 * the system security policy. The default PolicyLoader can be overridden - see
 * the {@link java.security.Policy} class documentation for further details.
 *
 * <h2>The access control algorithm</h2>
 *
 * <p>
 * As with J2SE, access permissions are checked against the currently executing
 * code 'context' - as defined by an {@link java.security.AccessControlContext}.
 * The context is gained by natively inspecting the current thread's call stack
 * and determining the distinct {@link java.security.ProtectionDomain}s for the
 * classes of the methods on the call stack.
 *
 * <p>
 * <code>ProtectionDomain</code> objects are created by the VM and assigned to
 * classes as they are loaded. As per J2SE, a protection domain identifies a
 * {@link java.security.CodeSource} with a set of static permissions. Static
 * permission are those permissions which are assigned by the VM or classloader
 * and are independent of the system security policy as held by the
 * {@link java.security.Policy}.
 *
 * <p>
 * When a <code>checkPermission(xxx)</code> is called on the SecurityManager,
 * the security manager begins by attaining the current execution context and
 * then for each ProtectionDomain contained in it, checking both the static
 * permissions on the ProtectionDomain and also the security policy held by the
 * Policy class. Additionally, the execution context that existed <i>at the time
 * the current thread was created</i> is also inspected. Any failure in access
 * permissions among any of these will cause a
 * {@link java.security.AccessControlException} to be thrown.
 *
 * <h2>Privileged Code</h2>
 *
 * Like J2SE, code may run other code as 'privileged' using the
 * {@link java.security.SecurityManager#doPrivileged(PrivilegedAction)} or
 * {@link java.security.SecurityManager#doPrivileged(PrivilegedExceptionAction)}
 * thus informing the security subsystem that the code is to be executed with
 * the permissions of the current calling code. In effect, this just limits the
 * depth of the stack inspection that looks for ProtectionDomains - that is,
 * when visiting all the methods on the current thread's call stack, only the
 * ProtectionDomains <i>above and including</i> the method calling the
 * privileged code should be considered for context inclusion.
 *
 * <p>
 * This does not mean that the permissions of any code called subsequently by
 * the privileged code are ignored - certainly not. Running privileged code does
 * not grant magical higher permissions to the privileged code - it just stops
 * the context creation algorithm going below the invoker of the privileged code
 * when finding ProtectionDomains. ProtectionDomain below the method invoking
 * "doPrivileged" are ignored.
 *
 * <p>
 * Not all of the J2SE "doPrivileged" methods are supported - those that specify
 * an {@link java.security.AccessControlContext} or a JAAS
 * <code>DomainCombiner</code> are not supported.
 *
 * <h2>Permissions</h2>
 *
 * Like J2SE, the access control framework centres around the
 * {@link java.security.Permission} class and its subclasses. Permission object
 * are used to both specify the system security policy, and to check against it.
 *
 * <p>
 * The J2SE defines the {@link java.security.BasicPermission} class, which is an
 * abstract subclass of the already-abstract {@link java.security.Permission}
 * class. The J2SE BasicPermission class defines the ability to namespace
 * permissions with rudimentry (but effective) wildcards. J2SE creates a number
 * of subclasses of BasicPermission to identify different security types. J2SE
 * BasicPermission provides the interface for permission 'actions' (like
 * read/write/open etc), but ignore them.
 *
 * <p>
 * In Babe, the <code>BasicPermission</code> class is concrete and uses actions.
 * In effect, the Babe <code>BasicPermission</code> class can replace most of
 * the J2SE <code>BasicPermission</code> subclasses just by namespacing the
 * permission name. For example, rather than define a (say)
 * <code>MyPermission</code> permission class with a number of actions, you may
 * be able to achieve the same with a <code>BasicPermission</code> using a
 * namespace like "myp.xxx". There is no requirement to do so, but it is there
 * as a measure to cut down on the number of permissions classes required. In
 * Babe, the <code>BasicPermission</code> class acts a lot like a mixture of the
 * J2SE <code>PropertyPermission</code> and <code>FilePermission</code> with
 * their combination of namespacing and actions. Now, having said that, the
 * {@link java.security.SecurityPermission} and
 * {@link java.security.RuntimePermission} are still present for the purposes of
 * clarity.
 *
 * <p>
 * In Babe, actions are represented by bits in an <code>int</code>. In J2SE,
 * actions are String objects and a Permission subclass with actions has to
 * parse the actions to make sense of them and then (generally) use an internal
 * <code>int</code> to store them - then on toString() it has to output them
 * back as Strings again. Lots of unnecessary code.
 *
 * <p>
 * The {@link java.security.Permission} class defines a number useful
 * pre-defined actions.
 *
 * <p>
 * The <code>Permission</code> class is still abstract in Babe and can be used
 * as the basis for any conceivable permission.
 *
 */
package java.security;

