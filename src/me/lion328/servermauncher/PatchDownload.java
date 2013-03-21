package me.lion328.servermauncher;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class PatchDownload {

	private String[] url;
	private JLabel label;
	private JProgressBar pbar;
	private String[] param;

	public PatchDownload(String par1[], JLabel par2, JProgressBar par3,
			String[] par4) {
		url = par1;
		label = par2;
		pbar = par3;
		param = par4;
	}

	public void download() {
		try {
			for (String thisUrl : url) {
				thisUrl = thisUrl.replace(" ", "%20");

				if (thisUrl.contains("-")) {
					System.out.println(thisUrl.substring(thisUrl
							.lastIndexOf("-")));
					String b = "";
					for (String a : Util.getOtherPlatform()) {
						b += "-" + a;
					}
					if (b.contains(thisUrl.substring(thisUrl.lastIndexOf("-")))) {
						continue;
					}
				}

				if (Util.isLatestPatchFile(LauncherSetting.minecraftDir
						+ thisUrl))
					continue;
				if (!new File(LauncherSetting.minecraftDir
						+ thisUrl.substring(0, thisUrl.lastIndexOf('/')))
						.isDirectory()) {
					new File(LauncherSetting.minecraftDir
							+ thisUrl.substring(0, thisUrl.lastIndexOf('/')))
							.mkdirs();
				}

				URL url = new URL(LauncherSetting.dlURL + thisUrl);
				URLConnection con = url.openConnection();
				DataInputStream in = new DataInputStream(con.getInputStream());
				thisUrl = thisUrl.replace("-" + Util.getPlatformString(), "");
				FileOutputStream out = new FileOutputStream(new File(
						LauncherSetting.minecraftDir + thisUrl));

				byte[] fileData = new byte[con.getContentLength()];

				int onepercent = fileData.length / 100;
				for (int x = 0; x < fileData.length; x++) {
					fileData[x] = in.readByte();
					if (onepercent != 0) {
						if (x % onepercent == 0) {
							label.setText("¡ÓÅÑ§´ÒÇ¹ìâËÅ´ "
									+ thisUrl.substring(thisUrl
											.lastIndexOf('/') + 1) + " " + x
									/ onepercent + "%");
							pbar.setValue(x / onepercent);
						}
					}
				}
				out.write(fileData);
				in.close();
				out.close();
			}
			Util.runMinecraft(param, 1024);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
