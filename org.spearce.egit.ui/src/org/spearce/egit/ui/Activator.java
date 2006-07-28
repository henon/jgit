/*
 *    Copyright 2006 Shawn Pearce <spearce@spearce.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.spearce.egit.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin
{
    private static Activator plugin;

    public static Activator getDefault()
    {
        return plugin;
    }

    public static String getPluginId()
    {
        return getDefault().getBundle().getSymbolicName();
    }

    public static CoreException error(final String message, final Throwable thr)
    {
        return new CoreException(new Status(
            IStatus.ERROR,
            getPluginId(),
            0,
            message,
            thr));
    }

    public static void logError(final String message, final Throwable thr)
    {
        getDefault().getLog().log(
            new Status(IStatus.ERROR, getPluginId(), 0, message, thr));
    }

    private static boolean isOptionSet(final String optionId)
    {
        final String option = getPluginId() + optionId;
        final String value = Platform.getDebugOption(option);
        return value != null && value.equals("true");
    }

    public static void trace(final String what)
    {
        if (getDefault().traceVerbose)
        {
            System.out.println("[" + getPluginId() + "] " + what);
        }
    }

    private boolean traceVerbose;

    public Activator()
    {
        plugin = this;
    }

    public void start(final BundleContext context) throws Exception
    {
        super.start(context);
        traceVerbose = isOptionSet("/trace/verbose");
    }

    public void stop(final BundleContext context) throws Exception
    {
        super.stop(context);
        plugin = null;
    }
}