# socket打印




```java 


public class SocketPrintUtil {

    private static final Logger LGR = LoggerFactory.getLogger(SocketPrintUtil.class);


	
	private String ip;
	private int port;

	public SocketPrintUtil(String ip) {
		super();
		this.ip = ip;
		this.port = ConfigUtil.getPrintPort();
	}
	public SocketPrintUtil(String ip,int port) {
		super();
		this.ip = ip;
		this.port =port;
	}
	public boolean printHead(String str) {
		try {
			Socket client = new java.net.Socket();
			PrintWriter socketWriter;
			client.setSoTimeout(1000);
			client.connect(new InetSocketAddress(ip, port), 1000); // 创建一个
			socketWriter = new PrintWriter(new OutputStreamWriter(client.getOutputStream(),"GBK"));// 创建输入输出数据流
//			InputStream is=client.getInputStream();
//			//获取Byte数组
//			byte[] a = null;
//			is.read(a);
			
			
			/* 纵向放大一倍 */
			socketWriter.write(0x1c);
			socketWriter.write(0x21);
			socketWriter.write(8);
			socketWriter.write(0x1b);
			socketWriter.write(0x21);
			socketWriter.write(8);
			socketWriter.println(str);
			
			
			
			socketWriter.flush();
			client.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}
	
	
	public boolean printHtml(String str, String code, int skip) {
		try {
			Socket client = new java.net.Socket();
			PrintWriter socketWriter;
			client.setSoTimeout(1000);
			client.connect(new InetSocketAddress(ip, port), 1000); // 创建一个
			socketWriter = new PrintWriter(new OutputStreamWriter(client.getOutputStream(),"GBK"));// 创建输入输出数据流
			/* 纵向放大一倍 */
			socketWriter.write(0x1c);
			socketWriter.write(0x21);
			socketWriter.write(8);
			socketWriter.write(0x1b);
			socketWriter.write(0x21);
			socketWriter.write(8);

			 byte[] CLEAR_FONT = new byte[3];
		     CLEAR_FONT[0] = 0x1c;
		     CLEAR_FONT[1] = 0x21;
		     CLEAR_FONT[2] = 1;
		     for (byte b : CLEAR_FONT) {
					socketWriter.write(b);
				}
		     
			socketWriter.println(str);
	
			for (int i = 0; i < skip; i++) {
				socketWriter.println(" ");// 打印完毕自动走纸
			}
			//设置切纸
			byte[] command = new byte[]{0x1D, 0x56, 66,2};
			for (byte b : command) {
				socketWriter.write(b);
			}
			socketWriter.flush();
			client.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}


	/**
	 * 开钱箱
	 * @param isClock
	 * @return
	 */
	public boolean openClock(boolean isClock) {
		try {
			Socket client = new java.net.Socket();
			PrintWriter socketWriter;
			client.connect(new InetSocketAddress(ip, port), 1000); // 创建一个
			socketWriter = new PrintWriter(new OutputStreamWriter(client.getOutputStream(),"GBK"));// 创建输入输出数据流

			//设置钱箱
			byte[] command = new byte[]{0x10, 0x14, 1,0,1};
			for (byte b : command) {
				socketWriter.write(b);
			}
			socketWriter.flush();
			client.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}


	/**
	 * 检查打印机状态
	 * @return
	 */
	public Map<String,Object> checkPrinterStatusBy4000(){
		Map<String,Object> checkStatus = new HashMap<>();
		String message = "";
		try {
			Socket client = new java.net.Socket();
			OutputStreamWriter socketWriter;
			client.connect(new InetSocketAddress(ip, port), 1000); // 创建一个
			socketWriter = new OutputStreamWriter(client.getOutputStream(),"GBK");// 创建输入输出数据流
	      	socketWriter.write(0x1B);
	      	socketWriter.write(0x76);
			socketWriter.flush();

			InputStream in= client.getInputStream();
			//获取Byte数组
			byte[] charBuf = new byte[4];
			int size = 0;
			size = in.read(charBuf , 0 , 4);
			for(int i=0;i<charBuf.length;i++){
				System.out.println( "byte + "+charBuf[i]);

				System.out.println("2进制 + "+getBinaryStrFromByte(charBuf[i]));
				char[] printChar = getBinaryStrFromByte(charBuf[i]).toCharArray();

				switch (i){
					case 0:
						if (printChar[7-3] == '1'){
							message = message +"【打印机不在线】";
						}
						if (printChar[7-5] == '1'){
							message = message +"【打印机上盖打开】";
						}

						break;
					case 1:
						if (printChar[7-3] == '1'){
							message = message +"【有切刀错误】";
						}
						if (printChar[7-5] == '1'){
							message = message +"【有不可恢复错误】";
						}
						if (printChar[7-6] == '1'){
							message = message +"【有可自动恢复错误发生】";
						}
						break;
					case 2:

						if (printChar[7-0] == '1' || printChar[7-1] == '1'){
							message = message +"【打印纸将尽】";
						}
						if (printChar[7-2] == '1' || printChar[7-3] == '1'){
							message = message +"【打印机缺少纸】";
						}
						break;
				}
			}
			if(message.equals("")){
				checkStatus.put("isPrinter",true);
			}else{
				checkStatus.put("isPrinter",false);
				checkStatus.put("message",message);
			}
			socketWriter.close();
			in.close();
			client.close();
			return checkStatus;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			checkStatus.put("isPrinter",false);
			checkStatus.put("message","无法寻找到目标主机，请检查网线或者配置~");
			return checkStatus;
		}

	}



	public String getBinaryStrFromByte(byte b){
		String result ="";
		byte a = b;
		for (int i = 0; i < 8; i++){
			byte c=a;
			a=(byte)(a>>1);//每移一位如同将10进制数除以2并去掉余数。
			a=(byte)(a<<1);
			if(a==c){
				result="0"+result;
			}else{
				result="1"+result;
			}
			a=(byte)(a>>1);
		}
		return result;
	}


	public static void main(String[] args) throws UnsupportedEncodingException {
//		SocketPrintUtil test = new SocketPrintUtil("192.168.1.66",9100);
		SocketPrintUtil test = new SocketPrintUtil("192.168.1.66",4000);
		;
		System.out.println(JSONObject.toJSONString(test.checkPrinterStatusBy4000()));
//		test.openClock(true);
//		StringBuffer teststr = new StringBuffer();
//		teststr.append("结账单\n");
//		teststr.append("包厢名称：007\n");
//		teststr.append("支付方式：现金（222.00）+信用卡（11.00）+抵用券（100.00）\n");
//		teststr.append("支付方式：现金（222.00）+信用卡（11.00）+抵用券（100.00）\n");
//		teststr.append("支付方式：现金（222.00）+信用卡（11.00）+抵用券（100.00）\n");


//		test.printHtml( teststr.toString(), "", 1);
//		



	}
	
}




```