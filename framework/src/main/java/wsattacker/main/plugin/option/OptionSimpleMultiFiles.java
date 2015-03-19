/**
 * WS-Attacker - A Modular Web Services Penetration Testing Framework Copyright
 * (C) 2010 Christian Mainka
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package wsattacker.main.plugin.option;

import java.io.File;
import wsattacker.main.composition.plugin.option.AbstractOptionMultiFiles;

public class OptionSimpleMultiFiles
    extends AbstractOptionMultiFiles
{

    private static final long serialVersionUID = 1L;

    public OptionSimpleMultiFiles( String name, String description )
    {
        super( name, description );
    }

    @Override
    public boolean isValid( File file )
    {
        return file != null && file.exists() && file.isFile();
    }
}
