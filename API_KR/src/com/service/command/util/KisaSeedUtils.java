package com.service.command.util;

import gsi.cm.app.extif.http.util.SeedUtil;

public class KisaSeedUtils {
	
	public static byte[] enc(String str, String encType, String encptKey) throws Exception {
        // int[] key = SeedUtil.getSeedRoundKey(encptKey);
        byte[] enc_data = (new SeedUtil().getSeedEncrypt(str, encptKey)).getBytes(encType);
        return enc_data; 
    }
    public static byte[] dec(byte[] str, String encType, String encptKey) throws Exception {
        // int[] key = SeedUtil.getSeedRoundKey(encptKey); 
        // 복호화 
        byte[] dec_data = (new SeedUtil().getSeedDecrypt(new String(str, encType), encptKey)).getBytes(encType);
        return dec_data; 
    }
}
