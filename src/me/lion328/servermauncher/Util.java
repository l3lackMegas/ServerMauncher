package me.lion328.servermauncher;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class Util {

	public static String authURL = LauncherSetting.authURL;

	public static void main(String args[]) {
		String[] a = getOtherPlatform();
		for (int i = 0; i < a.length; i++) {
			System.out.println(i + " " + a[i]);
		}
	}

	public static enum OS {
		LINUX, SOLARIS, WINDOWS, MACOS, UNKNOWN;
	}

	public static OS getPlatform() {
		String str = System.getProperty("os.name").toLowerCase();
		if (str.contains("win"))
			return OS.WINDOWS;
		if (str.contains("mac"))
			return OS.MACOS;
		if (str.contains("solaris"))
			return OS.SOLARIS;
		if (str.contains("sunos"))
			return OS.SOLARIS;
		if (str.contains("linux"))
			return OS.LINUX;
		if (str.contains("unix"))
			return OS.LINUX;
		return OS.UNKNOWN;
	}

	public static String getPlatformString() {
		String str = System.getProperty("os.name").toLowerCase();
		if (str.contains("win"))
			return "win";
		if (str.contains("mac"))
			return "macos";
		if (str.contains("solaris"))
			return "solaris";
		if (str.contains("sunos"))
			return "solaris";
		if (str.contains("linux"))
			return "linux";
		if (str.contains("unix"))
			return "linux";
		return "";
	}

	public static String[] getOtherPlatform() {
		return "win\nmacos\nsolaris\nlinux\n".replace(
				getPlatformString() + "\n", "").split("\n");
	}

	public static String getAppdata() {
		String userHome = System.getProperty("user.home", ".");
		if (getPlatform().equals(OS.MACOS))
			return userHome + "/Library/Application Support/";
		if (getPlatform().equals(OS.WINDOWS))
			return System.getenv("APPDATA");
		return userHome;
	}

	public static String getMinecraftDirectory() {
		return LauncherSetting.minecraftDir;
	}

	public static String getOfflineUsername() {
		String mill = Calendar.getInstance().getTimeInMillis() + "";
		return "Player" + mill.substring(mill.length() - 3);
	}

	public static void runMinecraft(int maxRam) {
		runMinecraft(new String[] { getOfflineUsername() }, maxRam);
	}

	public static void runMinecraft(String arg[], int maxRam) {
		runMinecraft(arg, getMinecraftDirectory() + "\\bin", maxRam);
	}

	public static void runMinecraft(String arg[], String mcBinDir, int maxRam) {
		runMinecraft(arg, new File(mcBinDir), maxRam);
	}

	public static void runMinecraft(String[] args, File mcBinDir, int maxRam) {
		ArrayList<String> params = new ArrayList<String>();

		params.add("java");
		params.add("-classpath");
		params.add("minecraft.jar;jinput.jar;lwjgl.jar;lwjgl_util.jar");
		params.add("-Xmx" + maxRam + "m");
		params.add("-Djava.library.path=natives");
		params.add("-Dfile.encoding=UTF-8");
		params.add("-Dsun.java2d.noddraw=true");
		params.add("-Dsun.java2d.d3d=false");
		params.add("-Dsun.java2d.opengl=false");
		params.add("-Dsun.java2d.pmoffscreen=false");
		params.add("net.minecraft.client.Minecraft");

		for (String parg : args) {
			params.add(parg);
		}

		ProcessBuilder procBuilder = new ProcessBuilder(params);
		procBuilder.directory(mcBinDir);
		try {
			procBuilder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean getCustomAuth(String username, String password) {
		String useAuthURL = authURL.replace("{0}", username).replace("{1}",
				password);
		return LauncherSetting.authURL.equals("") ? true : HTTPRequestPoster
				.sendGetRequest(useAuthURL, "").contains("true");
	}

	public static void readConfig() throws Exception {
		InputStream in = Util.class.getClass().getResourceAsStream(
				"/me/lion328/servermauncher/config.txt");
		BufferedReader fin = new BufferedReader(new InputStreamReader(in));
		String s;
		while ((s = fin.readLine()) != null) {
			if (s.trim().startsWith("#"))
				continue;
			String d[] = s.split("=");
			if (d.length > 2) {
				for (int i = 2; i <= d.length - 1; i++) {
					d[1] += "=" + d[i];
				}
			}
			if (d[0].trim().equals("minecraftDir"))
				LauncherSetting.minecraftDir = d[1].trim().replace("{APPDATA}",
						Util.getAppdata());
			if (d[0].trim().toLowerCase().equals("dlurl"))
				LauncherSetting.dlURL = d[1].trim();
			if (d[0].trim().toLowerCase().equals("authurl"))
				LauncherSetting.authURL = d[1].trim();
			if (d[0].trim().toLowerCase().equals("hashurl"))
				LauncherSetting.hashURL = d[1].trim();
			if (d[0].trim().toLowerCase().equals("txtnewsurl"))
				LauncherSetting.txtNewsURL = d[1].trim();
			if (d[0].trim().toLowerCase().equals("registerurl"))
				LauncherSetting.registerURL = d[1].trim();
			if (d[0].trim().toLowerCase().equals("title"))
				LauncherSetting.title = d[1].trim();
			if (d[0].trim().toLowerCase().equals("filelisturl"))
				LauncherSetting.fileListURL = d[1].trim();
		}
		fin.close();
	}

	public static void runURL(URI uri) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(uri);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void downloadPatch(JLabel label, JProgressBar pbar,
			String[] param) throws Exception {
		new PatchDownload(HTTPRequestPoster.sendGetRequest(
				LauncherSetting.fileListURL, "").split("<br>"), label, pbar,
				param).download();
	}

	public static boolean isLatestPatchFile(String filename) throws Exception {
		return new File(filename.replace("-win", "").replace("-macos", "")
				.replace("-solaris", "").replace("-linux", "")).exists() ? HTTPRequestPoster
				.sendGetRequest(
						LauncherSetting.hashURL
								.replace(
										"{0}",
										filename.replace(
												LauncherSetting.minecraftDir
														+ "/", ""))
								.replace(
										"{1}",
										MD5FileUtil.getMD5Checksum(
												filename.replace("-win", "")
														.replace("-macos", ""))
												.replace("-solaris", ""))
								.replace("-linux", ""), "").contains("true")
				: false;
	}

	public static String findPathJar(Class<?> context)
			throws IllegalStateException {
		URL location = context.getResource('/'
				+ context.getName().replace(".", "/") + ".class");
		String jarPath = location.getPath();
		return jarPath.substring("file:".length(), jarPath.lastIndexOf("!"))
				.substring(1);
	}

	public static String getSystemJavaExet() {
		if (getPlatform().equals(OS.WINDOWS)) {
			return ".exe";
		}
		return ".jar";
	}
}
