/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.testUtil;

/**
 * {@code IntegrationTests} is a marker interface that can be used to
 * categorize unit tests.  IntegrationTests interact with other systems
 * outside of this project.  An IntegrationTest failure might be due to network 
 * outages or problems with the external system(s) being interacted with.
 * @author dkatzel
 *
 *
 */
public interface IntegrationTests {

}
