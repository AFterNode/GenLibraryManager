package cn.afternode.genlibman.common.agent;

import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class GenLibManagerAgent {
    private static volatile Instrumentation inst = null;

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("[GenLibManager] Premain loaded");
        GenLibManagerAgent.inst = inst;
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("[GenLibManager] Agent loaded");
        GenLibManagerAgent.inst = inst;
    }

    public static Instrumentation getInstrumentation() {
        if (inst == null) {
            load();
        }
        return inst;
    }

    private static void load() {
        // Set property
        System.setProperty("jdk.attach.allowAttachSelf", "true");
//        System.out.println(System.getProperty("jdk.attach.allowAttachSelf"));

        // Get PID
        String vmName = ManagementFactory.getRuntimeMXBean().getName();
        String pid = vmName.substring(0, vmName.indexOf("@"));

        VirtualMachine vm = null;
        try {
            Class.forName(GenLibManagerAgent.class.getName());

            // Create manifest
            Manifest manifest = new Manifest();
            Attributes attr = manifest.getMainAttributes();
            attr.put(Attributes.Name.MANIFEST_VERSION, "1.0");
            attr.put(new Attributes.Name("Agent-Class"), GenLibManagerAgent.class.getName());

            // Create temp jar file
            File file = File.createTempFile("GenLibManagerAgent", ".jar");
            JarOutputStream jos = new JarOutputStream(Files.newOutputStream(file.toPath()), manifest);
            jos.flush();
            jos.close();

            // Attach
            vm = VirtualMachine.attach(pid);
            vm.loadAgent(file.getAbsolutePath());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            if (vm != null) {
                try {
                    vm.detach();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
