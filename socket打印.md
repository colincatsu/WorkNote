# socket��ӡ




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
			client.connect(new InetSocketAddress(ip, port), 1000); // ����һ��
			socketWriter = new PrintWriter(new OutputStreamWriter(client.getOutputStream(),"GBK"));// �����������������
//			InputStream is=client.getInputStream();
//			//��ȡByte����
//			byte[] a = null;
//			is.read(a);
			
			
			/* ����Ŵ�һ�� */
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
			client.connect(new InetSocketAddress(ip, port), 1000); // ����һ��
			socketWriter = new PrintWriter(new OutputStreamWriter(client.getOutputStream(),"GBK"));// �����������������
			/* ����Ŵ�һ�� */
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
				socketWriter.println(" ");// ��ӡ����Զ���ֽ
			}
			//������ֽ
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
	 * ��Ǯ��
	 * @param isClock
	 * @return
	 */
	public boolean openClock(boolean isClock) {
		try {
			Socket client = new java.net.Socket();
			PrintWriter socketWriter;
			client.connect(new InetSocketAddress(ip, port), 1000); // ����һ��
			socketWriter = new PrintWriter(new OutputStreamWriter(client.getOutputStream(),"GBK"));// �����������������

			//����Ǯ��
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
	 * ����ӡ��״̬
	 * @return
	 */
	public Map<String,Object> checkPrinterStatusBy4000(){
		Map<String,Object> checkStatus = new HashMap<>();
		String message = "";
		try {
			Socket client = new java.net.Socket();
			OutputStreamWriter socketWriter;
			client.connect(new InetSocketAddress(ip, port), 1000); // ����һ��
			socketWriter = new OutputStreamWriter(client.getOutputStream(),"GBK");// �����������������
	      	socketWriter.write(0x1B);
	      	socketWriter.write(0x76);
			socketWriter.flush();

			InputStream in= client.getInputStream();
			//��ȡByte����
			byte[] charBuf = new byte[4];
			int size = 0;
			size = in.read(charBuf , 0 , 4);
			for(int i=0;i<charBuf.length;i++){
				System.out.println( "byte + "+charBuf[i]);

				System.out.println("2���� + "+getBinaryStrFromByte(charBuf[i]));
				char[] printChar = getBinaryStrFromByte(charBuf[i]).toCharArray();

				switch (i){
					case 0:
						if (printChar[7-3] == '1'){
							message = message +"����ӡ�������ߡ�";
						}
						if (printChar[7-5] == '1'){
							message = message +"����ӡ���ϸǴ򿪡�";
						}

						break;
					case 1:
						if (printChar[7-3] == '1'){
							message = message +"�����е�����";
						}
						if (printChar[7-5] == '1'){
							message = message +"���в��ɻָ�����";
						}
						if (printChar[7-6] == '1'){
							message = message +"���п��Զ��ָ���������";
						}
						break;
					case 2:

						if (printChar[7-0] == '1' || printChar[7-1] == '1'){
							message = message +"����ӡֽ������";
						}
						if (printChar[7-2] == '1' || printChar[7-3] == '1'){
							message = message +"����ӡ��ȱ��ֽ��";
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
			checkStatus.put("message","�޷�Ѱ�ҵ�Ŀ���������������߻�������~");
			return checkStatus;
		}

	}



	public String getBinaryStrFromByte(byte b){
		String result ="";
		byte a = b;
		for (int i = 0; i < 8; i++){
			byte c=a;
			a=(byte)(a>>1);//ÿ��һλ��ͬ��10����������2��ȥ��������
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
//		teststr.append("���˵�\n");
//		teststr.append("�������ƣ�007\n");
//		teststr.append("֧����ʽ���ֽ�222.00��+���ÿ���11.00��+����ȯ��100.00��\n");
//		teststr.append("֧����ʽ���ֽ�222.00��+���ÿ���11.00��+����ȯ��100.00��\n");
//		teststr.append("֧����ʽ���ֽ�222.00��+���ÿ���11.00��+����ȯ��100.00��\n");


//		test.printHtml( teststr.toString(), "", 1);
//		



	}
	
}




```