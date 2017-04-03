package me.lion328.servermauncher;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class PatchDownload extends Thread{

	private String[] url;
	private JLabel label;
	private JProgressBar pbar;
	private String[] param;

	public PatchDownload(String par0, String par1[], JLabel par2, JProgressBar par3,
			String[] par4) {
		super(par0);
		url = par1;
		label = par2;
		pbar = par3;
		param = par4;
	}

	public void run() {
		Util.log("Downloading patch...");
		try {
			for (String thisUrl : url) {
				thisUrl = thisUrl.replace(" ", "%20");

				if (thisUrl.contains("-")) {
					String b = "";
					for (String a : Util.getOtherPlatform()) {
						b += "-" + a;
					}
					if (b.contains(thisUrl.substring(thisUrl.lastIndexOf("-")))) {
						continue;
					}
				}

				if (Util.isLatestPatchFile(LauncherSetting.minecraftDir+ thisUrl)){
					Util.log("File \""+thisUrl+"\" is latest version.");
					label.setText("����� "+thisUrl+" ���������ش");
					pbar.setValue(100);
					continue;
				} else {
					Util.log("File \""+thisUrl+"\" is outdated version. Updating...");
					label.setText("����� "+thisUrl+" ������������ش ���ѧ��ǹ���Ŵ...");
				}
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
				Util.log("Downloading \""+thisUrl+"\" ...");
				for (int x = 0; x < fileData.length; x++) {
					fileData[x] = in.readByte();
					if (onepercent != 0) {
						if (x % onepercent == 0) {
							label.setText("���ѧ��ǹ���Ŵ "
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
			Util.log("Running game...");
			label.setText("�ӧҹ������� ���ѧ�������...");
			Util.runMinecraft(param, 1024);
			Util.saveLog();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
