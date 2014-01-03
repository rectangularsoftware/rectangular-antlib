package com.rectangularsoftware.antlib.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Custom task that uses the ADB tool to install a specified APK on all connected devices
 * and emulators.
 * @author Daniel Dyer
 */
public class InstallAPK extends Task
{
    private File apkFile;

    public void setAPK(File apkFile)
    {
        this.apkFile = apkFile;
    }


    @Override
    public void execute() throws BuildException
    {
        checkArguments();
        try
        {
            List<String> devices = getDeviceIdentifiers();
            for (String device : devices)
            {
                System.out.printf("Installing APK on %s...%n", device);
                Process process = Runtime.getRuntime().exec(new String[]{"adb", "-s", device, "install", "-r", apkFile.toString()});
                consumeStream(process.getInputStream(), System.out);
                if (process.waitFor() != 0)
                {
                    consumeStream(process.getErrorStream(), System.err);
                    throw new BuildException(String.format("Installing APK on %s failed.", device));
                }
            }
        }
        catch (IOException ex)
        {
            throw new BuildException(ex);
        }
        catch (InterruptedException ex)
        {
            throw new BuildException(ex);
        }
    }


    private void consumeStream(InputStream in, PrintStream out) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try
        {
            for (String line = reader.readLine(); line != null; line = reader.readLine())
            {
                out.println(line);
            }
        }
        finally
        {
            reader.close();
        }
    }


    private List<String> getDeviceIdentifiers() throws IOException, InterruptedException
    {
        Process process = Runtime.getRuntime().exec("adb devices");
        List<String> devices = new ArrayList<String>(10);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        try
        {
            for (String line = reader.readLine(); line != null; line = reader.readLine())
            {
                if (line.endsWith("device"))
                {
                    devices.add(line.split("\\s")[0]);
                }
            }
            if (process.waitFor() != 0)
            {
                consumeStream(process.getErrorStream(), System.err);
                throw new BuildException("Failed getting list of connected devices/emulators.");
            }
        }
        finally
        {
            reader.close();
        }
        return devices;
    }


    private void checkArguments() throws BuildException
    {
        if (apkFile == null)
        {
            throw new BuildException("APK file must be specified");
        }
    }
}
