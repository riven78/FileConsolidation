package com.riven.fileutils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class DigestUtils {

	public static class FileEx {
		private File file;
		private String md5, sha512;

		public boolean isEmpty() {
			boolean ret = true;
			if (file != null && file.exists() && file.isFile()) {
				ret = false;
			}
			return ret;
		}

		public FileEx(File file) {
			this.file = file;
		}

		public long getFileSize() {
			long ret = 0L;
			if (!isEmpty()) {
				ret = file.length();
			}
			return ret;
		}

		public String getMD5() {
			if (!isEmpty() && (md5 == null || md5.length() == 0)) {
				md5 = getFileMD5(file);
			}
			return md5;
		}

		public String getSha512() {
			if (!isEmpty() && (sha512 == null || sha512.length() == 0)) {
				sha512 = getFileSha512(file);
			}
			return sha512;
		}

		public File getFile() {
			return file;
		}

	}

	public static boolean equalFile(FileEx fileEx1, FileEx fileEx2) {
		boolean ret = false;
		if (fileEx1 != null && fileEx2 != null && !fileEx1.isEmpty() && !fileEx2.isEmpty()) {
			ret = true;
			if (fileEx1.getFileSize() != fileEx2.getFileSize()
					|| !equalFileContent(fileEx1.getFile(), fileEx2.getFile())) {
				ret = false;
			}
		}
		return ret;
	}

	public static boolean equalFileContent(File file1, File file2) {
		boolean ret = false;
		if (file1 != null && file2 != null && file1.exists() 
				&& file1.isFile() && file2.exists() && file2.isFile()) {
			ret = true;
			if (!file1.getAbsoluteFile().equals(file2.getAbsoluteFile())) {
				FileInputStream fis1 = null;
				FileInputStream fis2 = null;
				try {
					fis1 = new FileInputStream(file1);
					fis2 = new FileInputStream(file2);

					int len1 = fis1.available();// 返回总的字节数
					int len2 = fis2.available();

					if (len1 == len2) {// 长度相同，则比较具体内容
						// 建立两个字节缓冲区
						byte[] data1 = new byte[1024];
						byte[] data2 = new byte[1024];

						// 分别将两个文件的内容读入缓冲区
						while ((len1 = fis1.read(data1)) > 0 && (len2 = fis2.read(data2)) > 0 && ret) {
//							System.out.println("len1=" + len1 + ";len2=" + len2);
							if (len1 == len2) {
								for (int i = 0; i < len1; i++) {
									// 只要有一个字节不同，两个文件就不一样
									if (data1[i] != data2[i]) {
										System.out.println("文件内容不一样");
										ret = false;
										break;
									}
								}
							} else {
								ret = false;
							}
						}
					} else {
						ret = false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {// 关闭文件流，防止内存泄漏
					if (fis1 != null) {
						try {
							fis1.close();
						} catch (Exception e) {
							// 忽略
							e.printStackTrace();
						}
					}
					if (fis2 != null) {
						try {
							fis2.close();
						} catch (Exception e) {
							// 忽略
							e.printStackTrace();
						}
					}
				}
			}
		}
		return ret;
	}

	// 计算文件的 MD5 值
	public static String getFileMD5(File file) {
		String ret = null;
		if (file.isFile()) {
			MessageDigest digest = null;
			FileInputStream in = null;
			byte buffer[] = new byte[8192];
			int len;
			try {
				digest = MessageDigest.getInstance("MD5");
				in = new FileInputStream(file);
				while ((len = in.read(buffer)) != -1) {
					digest.update(buffer, 0, len);
				}
				BigInteger bigInt = new BigInteger(1, digest.digest());
				ret = bigInt.toString(16);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// System.out.format("getFileMD5=%s", ret);
		return ret;
	}

//计算文件的SHA-512 值
	public static String getFileSha512(File file) {
		String ret = null;
		if (file.isFile()) {
			MessageDigest digest = null;
			FileInputStream in = null;
			byte buffer[] = new byte[8192];
			int len;
			try {
				digest = MessageDigest.getInstance("SHA-512");
				in = new FileInputStream(file);
				while ((len = in.read(buffer)) != -1) {
					digest.update(buffer, 0, len);
				}
				BigInteger bigInt = new BigInteger(1, digest.digest());
				ret = bigInt.toString(16);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
//		System.out.format("\ngetFileSha512=%s", ret);
		return ret;
	}

//计算文件的 SHA-1 值
	public static String getFileSha1(File file) {
		String ret = null;
		if (file.isFile()) {
			MessageDigest digest = null;
			FileInputStream in = null;
			byte buffer[] = new byte[8192];
			int len;
			try {
				digest = MessageDigest.getInstance("SHA-1");
				in = new FileInputStream(file);
				while ((len = in.read(buffer)) != -1) {
					digest.update(buffer, 0, len);
				}
				BigInteger bigInt = new BigInteger(1, digest.digest());
				ret = bigInt.toString(16);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	/**
	 * 合并两个byte数组
	 * 
	 * @param pByteA
	 * @param pByteB
	 * @return
	 */
	public static byte[] getMergeBytes(byte[] pByteA, byte[] pByteB) {
		int aCount = pByteA.length;
		int bCount = pByteB.length;
		byte[] b = new byte[aCount + bCount];
		for (int i = 0; i < aCount; i++) {
			b[i] = pByteA[i];
		}
		for (int i = 0; i < bCount; i++) {
			b[aCount + i] = pByteB[i];
		}
		return b;
	}
}
