/**
 * This file is part of Ogar.
 *
 * Ogar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ogar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Ogar.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ogarproject.ogar.server.util.thread;

import org.apache.log4j.Logger;

/**
 * @author VISTALL
 * @date 19:13/04.04.2011
 */
public abstract class RunnableImpl implements Runnable
{
        private static final Logger _log = Logger.getLogger(RunnableImpl.class);
        protected abstract void runImpl() throws Exception;

        @Override
        public final void run()
        {
                try
                {
                        runImpl();
                }
                catch(Exception e)
                {
                        _log.error("Exception: RunnableImpl.run():", e);
                        e.printStackTrace();
                }
        }
}