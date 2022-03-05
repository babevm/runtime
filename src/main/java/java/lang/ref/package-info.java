/**
 * <p>
 * Provide CLDC 1.1 compliant weak reference support.
 *  
 * <p>
 * CDLC weak reference support is limited to an abstract {@link Reference} class and a single concrete 
 * {@link WeakReference} class.  Unlike J2SE weak reference support that provides for varying degrees of weakness and 
 * notification of collection and so on, the CLDC weak references simply have their held referenced <code>null</code>ed 
 * when object it refers to have been collected.
 * 
 * <p>
 * Any larger structures based on weak reference support, such as a weak collection, must check manually for <code>null</code>ed 
 * weak references.
 */
package java.lang.ref;