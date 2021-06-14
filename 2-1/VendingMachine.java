/*
 * [ 자판기 관리 프로그램 ]
 * 
 *  1. 프로그램 명: VendingMachine
 *  
 *  2. 작성자: 20194056 이선아
 *  
 *  3. 프로그램 구성
 *     # 메인 화면 - 메뉴 선택 버튼
 *     			- 금액 확인 창, 잔돈 반환 레버, 음료 배출구
 * 				- 화폐 투입 버튼
 * 				- 관리자 메뉴 버튼 - 비밀번호 입력
 *     # 관리자 - 재고 관리
 *     		  - 화폐 관리
 *     		  - 메뉴 설정
 *     		  - 매출 산출
 *     		  - 비밀번호 설정
 *     
 *  4-1. 프로그램 실행 환경
 *      - CPU: Intel(R) Core(TM) i3-8130U
 *      
 *  4-2. 프로그램 개발 환경
 *  	- Windows 10 Version 2004 x64
 *      - Eclipse IDE 20-06
 *      - JRE System Library[JavaSE-14] 
 * 
 *  5. 히스토리
 *     - 메인 화면 기본 틀 구성(메뉴버튼, 금액 표시 창, 잔돈 반환 레버, 화폐 버튼): GridLayout
 *     - 금액 가감 구현, 재고 감소 구현, 메뉴 이미지 추가, 화폐 이미지 추가, 레버 이미지 추가, 배출구 이미지 추가
 *     - 재고와 금액에 따른 메뉴 이벤트 여부 구현
 *     - 잔돈 반환 팝업 다이얼로그 구현, 관리자 버튼 추가
 *     - 관리자 다이얼로그 구현, 내부 메뉴 버튼 구성(재고 관리, 화폐 관리, 메뉴 설정, 매출 산출, 비밀번호 설정): Card Layout
 *     - 메인 화면 레이아웃 변경(GridLayout → BorderLayout)
 *	   - 데이터 구조 재구성 (→ 배열화 Drink[], drinkButton[], Menu[] ...)
 *	   - 자판기 내 화폐 가감 구현
 *	   - 재고 관리 메뉴 구성(스피너), Card Layout 전환 기능
 *	   - 화폐 관리 메뉴 구성(스피너)
 *	   - 메뉴 설정 메뉴 구성(텍스트 필드)
 *     - 관리자 메뉴 실행시 비밀번호 입력 다이얼로그 추가
 *     - PW 클래스 메소드 내용 변경, 비밀번호 변경 메뉴 구성(패스워드 필드), 비밀번호 입력 다이얼로그 수정
 *     - 매출 산출 메뉴 구성(콤보박스, 테이블), 테이블 파일 연결 및 출력
 *	   - 메뉴 버튼 이벤트 파일 입력 클래스 추가
 * 	   - 매출_일별.txt, 매출_월별.txt, 재고소진_txt 연결 오류 정비
 *     - catch error → 에러 다이얼로그 set 
 */

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.InputMismatchException;
import java.io.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.Date;

//Drink: 메뉴 음료의 정보를 저장
class Drink{			
	String name;		// 음료 이름
	int price;			// 음료 가격
	int amount;			// 음료 재고 수
	
	ImageIcon state1;	// 기본 상태
	ImageIcon state2;	// 선택 불가 상태(금액 부족)
	ImageIcon state3;	// 매진 상태
	ImageIcon now;		// 현재 상태
	
	Drink(String name, int price){
		this.name=name;			// 음료 메뉴 이름 설정
		this.price=price;		// 음료 메뉴 판매 가격 설정
		this.amount=3;		// 기본 재고 수량 설정
	}
	void changeName(String newName) {	// 음료 메뉴 이름 변경
		this.name=newName;	
	}
	void changePrice(int newPrice) {	// 음료 메뉴 가격 변경
		this.price=newPrice;
	}
}

// Money: 화폐 단위 저장
class Money{
	int W1000;		// 각 화폐 수량 저장
	int W500;
	int W100;
	int W50;
	int W10;
	
	Money(){
		try {
			// 파일에서 화폐 수 읽어오기
			File f = new File("./화폐.txt");
			FileReader fr = new FileReader(f);
			String data[] = new String[5];
			Scanner scanner = new Scanner(fr);	// Scanner - file 연결
			for(int i=0;i<5;i++) {		// file 문자열 데이터로 읽기
				data[i]=scanner.next();
			}
			W1000 = Integer.parseInt(data[0]);	// (문자열 → 정수형) 읽어은 화폐 수 객체에 저장
			W500 = Integer.parseInt(data[1]);
			W100 = Integer.parseInt(data[2]);
			W50 = Integer.parseInt(data[3]);
			W10 = Integer.parseInt(data[4]);
			
			scanner.close();
			fr.close();
		}
		catch(IOException e) {		// 화폐 파일 없음!
			new ExceptionDialog("file not found! :: 화폐.txt\n 기본 재고수로 설정");	// 안내 다이얼로그 열기
			W1000=W500=W100=W50=W10=5;		// 파일이 없어 기본 수량으로 5개 설정
		}
	}
	public int getTotal() {		// 화폐 수량에 따른 전체 금액 계산
		int sum;
		sum=(1000*W1000)+(500*W500)+(100*W100)+(50*W50)+(10*W10);
		return sum;
	}
}

// 비밀번호
class PW {
	String PassWord;

// 기본 비밀번호 설정 -> 파일 읽어오기
	PW(){	
		File file = new File("./pw.txt");
		try{	// 비밀번호 읽기 및 저장 → PassWord
			FileReader fr = new FileReader(file);
			Scanner scanner = new Scanner(file);
			PassWord=scanner.nextLine();
			scanner.close();
			fr.close();
		}
		catch(IOException e){	// 비밀번호 파일 없음!
			new ExceptionDialog("file not found! :: pw.txt");	// 안내 다이얼로그 열기
			PassWord="0000000!";
		}
		
	}
	
// 비밀번호 입력(로그인)	
	boolean checkPW(String text) {	
		if(PassWord.equals(text))	// 일치 여부 검사
			return true;
		else
			return false;
	}
	
// text가 비밀번호가 될 수 있는가?
	boolean isPW(String text) {		
		int i;	char c;
		boolean isNumber=false, isSpecialC=false;	// isNumber: 숫자 포함 여부 isSpecialC: 특수문자 포함 여부
		
		for(i=0;i<text.length();i++) {		// 문자 검사
			c= text.charAt(i);	// 해당 인덱스의 글자 반환
			
			if(c=='1'||c=='2'||c=='3'||c=='4'||c=='5'||c=='6'||c=='7'||c=='8'||c=='9'||c=='0')
				isNumber=true;		// 해당 비밀번호는 숫자가 있음
			else if(c=='~'||c=='`'||c=='!'||c=='@'||c=='#'||c=='$'||c=='%'||c=='^'||c=='&'||c=='*'||
					c=='('||c==')'||c=='-'||c=='_'||c=='+'||c=='=')
				isSpecialC=true;	// 해당 비밀번호는 특수문자가 있음
		}
		if(isNumber==false || isSpecialC==false || text.length()<8) 	// 비밀번호 실패 조건
			return false;	// 조건 불충족
		
		else	// 조건 충족
			return true;
	}
	
// 비밀번호 변경 메소드
	void changePW(String text) {	
		PassWord=text;
		try{
			FileWriter fout = new FileWriter("./pw.txt");	// FileWriter: 덮어쓰기
			fout.write(text);
			fout.close();
		}
		catch(IOException e) {		// 파일이 없음
			new ExceptionDialog("file not found! :: pw.txt");	// 안내 다이얼로그 열기
		}		
	}
}

class Pay{		// 자판기에 지불된 화폐 클래스
	int total;			// 총 지불금액
	boolean payCoin;	// 동전 지불 여부
	Pay(){
		total=0;
		payCoin = false;
	}
}

class ExceptionDialog   {		// 안내 다이얼로그 
	JDialog d = new JDialog();
	ExceptionDialog(String text){		// 입력된 text를 JLabel에 입력
		d.setSize(300, 150);
		d.setLayout(new BorderLayout());	// BorderLayout-CENTER: text, SOUTH: ok button
		
		JPanel p = new JPanel();
		JLabel l = new JLabel(text);	// text 입력받음
		JButton b = new JButton("OK");	// OK button 생성
		
		l.setFont(new Font("나눔스퀘어", Font.PLAIN, 15));	// JLabel 설정
		l.setHorizontalAlignment(SwingConstants.CENTER);
		l.setVerticalAlignment(SwingConstants.CENTER);
		b.setBackground(Color.orange);
		b.addActionListener(new ActionListener() {	// OK버튼 선택 → 다이얼로그 닫기
			public void actionPerformed(ActionEvent v) {
				d.setVisible(false);
			}
		});
		p.add(l);
		d.add(p, BorderLayout.CENTER);
		d.add(b, BorderLayout.SOUTH);
		
		d.setVisible(true);
		d.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}

///////////////////////////////////// VendingMachine /////////////////////////////////////

public class VendingMachine extends JFrame{
	final int BasisMoney = 5;		// 기본 화페 수
	Money VMoney = new Money();	// 자판기 내 화폐
	
	Pay pay = new Pay();
	
	ImageIcon imageicon;	Image img;		// 이미지 사이즈 변환에 사용
	
	// 음료 관련
	Drink[] drink = new Drink[5];	// 메뉴 배열
	JButton [] drinkButton = new JButton[5];	// 메뉴 선택 버튼 배열
	JLabel [] pricelabel = new JLabel[drink.length];	// 메뉴 버튼 - 메뉴정보 표기
	
	JTextField leftPayText = new JTextField();		// 금액 표기 창
	
	JLabel Take = new JLabel();				// 음료 배출구 레이블
	ImageIcon [] TakeDrink = new ImageIcon[6];	// 배출구 이미지 배열
	
	PW password;	// 비밀번호
	
	public VendingMachine() {
		setTitle("VendingMachine 20194056");		// 프레임 타이틀
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// 윈도우 창 X - 전체 프로그램 종료
		
		Container c = getContentPane();
		setSize(1200, 900);						// 프레임 사이즈
		setResizable(false);						// 프레임 사이즈 변경 불가
		
		JPanel mainPanel = new JPanel();		// 메인 패널
		mainPanel.setBackground(Color.orange); 			// 메인 패널 색상 설정
		c.add(mainPanel);
		
		drink[0] = new Drink("물", 450);			// 각 메뉴 객체 생성
		drink[1] = new Drink("커피", 500);
		drink[2] = new Drink("이온음료", 550);
		drink[3] = new Drink("고급커피", 700);
		drink[4] = new Drink("탄산음료", 750);
		
		File fileAmount = new File("./재고.txt");
		try {
			FileReader fr = new FileReader(fileAmount);
			Scanner scanner = new Scanner(fr);
			for(int i=0;i<5;i++)
				drink[i].amount=Integer.parseInt(scanner.next());		// 파일에서 재고수 읽기
			fr.close(); 	scanner.close();
		}
		catch(IOException e) {		// 재고 파일 없음!
			new ExceptionDialog("file not found! :: 재고.txt");	// 안내 다이얼로그 열기
		}
		
			// 메뉴 state 이미지 설정
			int drinkW=200;	int drinkH=220;
			drink[0].state1 = new ImageIcon("./image/음료1_물_1.png");		// 물
			img=drink[0].state1.getImage();	img=img.getScaledInstance(drinkW, drinkH, Image.SCALE_SMOOTH);
			drink[0].state1.setImage(img);
			drink[0].state2 = new ImageIcon("./image/음료1_물_2.png");
			img=drink[0].state2.getImage();	img=img.getScaledInstance(drinkW, drinkH, Image.SCALE_SMOOTH);
			drink[0].state2.setImage(img);
			drink[0].state3 = new ImageIcon("./image/음료1_물_3.png");
			img=drink[0].state3.getImage();	img=img.getScaledInstance(drinkW, drinkH, Image.SCALE_SMOOTH);
			drink[0].state3.setImage(img);
			drink[1].state1 = new ImageIcon("./image/음료2_커피_1.png");		// 커피
			img=drink[1].state1.getImage();	img=img.getScaledInstance(drinkW, drinkH, Image.SCALE_SMOOTH);
			drink[1].state1.setImage(img);
			drink[1].state2 = new ImageIcon("./image/음료2_커피_2.png");	
			img=drink[1].state2.getImage();	img=img.getScaledInstance(drinkW, drinkH, Image.SCALE_SMOOTH);
			drink[1].state2.setImage(img);
			drink[1].state3 = new ImageIcon("./image/음료2_커피_3.png");	
			img=drink[1].state3.getImage();	img=img.getScaledInstance(drinkW, drinkH, Image.SCALE_SMOOTH);
			drink[1].state3.setImage(img);
			drink[2].state1 = new ImageIcon("./image/음료3_이온음료_1.png");	// 이온음료
			img=drink[2].state1.getImage();	img=img.getScaledInstance(drinkW, drinkH, Image.SCALE_SMOOTH);
			drink[2].state1.setImage(img);
			drink[2].state2 = new ImageIcon("./image/음료3_이온음료_2.png");	
			img=drink[2].state2.getImage();	img=img.getScaledInstance(drinkW, drinkH, Image.SCALE_SMOOTH);
			drink[2].state2.setImage(img);
			drink[2].state3 = new ImageIcon("./image/음료3_이온음료_3.png");	
			img=drink[2].state3.getImage();	img=img.getScaledInstance(drinkW, drinkH, Image.SCALE_SMOOTH);
			drink[2].state3.setImage(img);
			drink[3].state1 = new ImageIcon("./image/음료4_고급커피_1.png");	// 고급커피
			img=drink[3].state1.getImage();	img=img.getScaledInstance(drinkW, drinkH, Image.SCALE_SMOOTH);
			drink[3].state1.setImage(img);
			drink[3].state2 = new ImageIcon("./image/음료4_고급커피_2.png");	
			img=drink[3].state2.getImage();	img=img.getScaledInstance(drinkW, drinkH, Image.SCALE_SMOOTH);
			drink[3].state2.setImage(img);
			drink[3].state3 = new ImageIcon("./image/음료4_고급커피_3.png");	
			img=drink[3].state3.getImage();	img=img.getScaledInstance(drinkW, drinkH, Image.SCALE_SMOOTH);
			drink[3].state3.setImage(img);
			drink[4].state1 = new ImageIcon("./image/음료5_탄산음료_1.png");	// 탄산음료
			img=drink[4].state1.getImage();	img=img.getScaledInstance(drinkW, drinkH, Image.SCALE_SMOOTH);
			drink[4].state1.setImage(img);
			drink[4].state2 = new ImageIcon("./image/음료5_탄산음료_2.png");	
			img=drink[4].state2.getImage();	img=img.getScaledInstance(drinkW, drinkH, Image.SCALE_SMOOTH);
			drink[4].state2.setImage(img);
			drink[4].state3 = new ImageIcon("./image/음료5_탄산음료_3.png");	
			img=drink[4].state3.getImage();	img=img.getScaledInstance(drinkW, drinkH, Image.SCALE_SMOOTH);
			drink[4].state3.setImage(img);
			for(int i=0;i<=4;i++) {
				if(drink[i].amount>0)	drink[i].now=drink[i].state2;	// 매진
				else					drink[i].now=drink[i].state3;	// 기본(돈X)	
			}
		
		// 음료 메뉴 버튼 설정
		for(int i=0;i<=4;i++) {
			drinkButton[i] = new JButton(drink[i].name, drink[i].now);
			drinkButton[i].setPreferredSize(new Dimension(drinkW, drinkH+30)); 	// 버튼 사이즈
			drinkButton[i].setFocusPainted(false);
			drinkButton[i].setBackground(Color.white); 							// 버튼 배경색 설정
			drinkButton[i].addActionListener(new DrinkButtonAction()); 			// 음료 버튼 이벤트
			drinkButton[i].setVerticalAlignment(SwingConstants.NORTH);
			drinkButton[i].setHorizontalAlignment(SwingConstants.CENTER);
			pricelabel[i] = new JLabel();
			pricelabel[i].setText(drink[i].name+" "+drink[i].price+"원");
			pricelabel[i].setFont(new Font("나눔스퀘어", Font.PLAIN, 20));
			pricelabel[i].setHorizontalAlignment(SwingConstants.CENTER);
			drinkButton[i].setLayout(new BorderLayout());
			drinkButton[i].add(pricelabel[i], BorderLayout.SOUTH);
		}
		
		// 자판기 화면(1): 메뉴 선택
		JPanel Panel1 = new JPanel();
		Panel1.setBackground(Color.white);
			// 음료 선택 창
			JPanel menuSelect = new JPanel();
			menuSelect.setBackground(Color.white);
			menuSelect.setLayout(new GridLayout(3,2,0,0));	// 메뉴 선택 패널: 그리드
			for(int i=0;i<drinkButton.length;i++)
				menuSelect.add(drinkButton[i]);
		Panel1.add(menuSelect);	
		mainPanel.add(Panel1);		// 메인 패널에 추가
		
			// 금액창, 레버, 배출구 화면(2)
			JPanel Panel1_right = new JPanel();
			Panel1_right.setBackground(Color.white);
			Panel1_right.setLayout(new BoxLayout(Panel1_right, BoxLayout.Y_AXIS));	// 수직방향 정렬
				// 금액창
				JPanel panel_left = new JPanel();
				panel_left.setBackground(Color.white);
				
				leftPayText.setEditable(false); 		// 금액창 수정 불가
				leftPayText.setFont(new Font("Consolas", Font.BOLD, 50));
				leftPayText.setBackground(Color.black);
				leftPayText.setForeground(new Color(59,255,92));
				leftPayText.setHorizontalAlignment(SwingConstants.RIGHT);
				leftPayText.setPreferredSize(new Dimension(200, 70));	// 크기 조절
				leftPayText.setText(" "+ pay.total+" ");	// 내용 설정
				panel_left.add(leftPayText);
			Panel1_right.add(panel_left);
		
				//잔돈 반환 레버
				JPanel panel_lever = new JPanel();
				panel_lever.setBackground(Color.white);
				
				ImageIcon Lever1 = new ImageIcon("./image/레버1.png");	// 레버 이미지 설정
				img=Lever1.getImage();	img=img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
				Lever1.setImage(img);
				ImageIcon Lever2 = new ImageIcon("./image/레버2.png");
				img=Lever2.getImage();	img=img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
				Lever2.setImage(img);
				
				JButton changeLever = new JButton(Lever1);		// 레버 버튼
				changeLever.setBackground(Color.white);
				changeLever.setPressedIcon(Lever2); 	// 클릭시 이미지 변환
				changeLever.setBorderPainted(false);
				changeLever.setFocusPainted(false);
				changeLever.setContentAreaFilled(false);
				changeLever.addActionListener(new changeLeverAction()); 		// 레버 버튼 이벤트
				panel_lever.add(changeLever);
			Panel1_right.add(panel_lever);	
			
				// 음료 배출구
				JPanel panel_take = new JPanel();
				panel_take.setBackground(Color.white);
					// 배출구 이미지
					TakeDrink[0]=new ImageIcon("./image/배출구_물.png");
					TakeDrink[1]=new ImageIcon("./image/배출구_커피.png");
					TakeDrink[2]=new ImageIcon("./image/배출구_이온음료.png");
					TakeDrink[3]=new ImageIcon("./image/배출구_고급커피.png");
					TakeDrink[4]=new ImageIcon("./image/배출구_탄산음료.png");
					TakeDrink[5]=new ImageIcon("./image/배출구.png");
				Take.setIcon(TakeDrink[5]);
				panel_take.add(Take);
			Panel1_right.add(panel_take);
		Panel1.add(Panel1_right);
			// 빈 패널
			JPanel Panel1_null = new JPanel();	Panel1_null.setBackground(Color.white);
			Panel1_right.add(Panel1_null);
		mainPanel.add(Panel1_right);
		
		
		// 화폐 투입 화면(3)
		JPanel Panel2 = new JPanel();
		Panel2.setBackground(Color.orange);
		Panel2.setLayout(new BoxLayout(Panel2, BoxLayout.Y_AXIS));
			
			// 투입구 이미지
			JLabel EntryGate = new JLabel(new ImageIcon("./image/지폐투입구.png"));
			EntryGate.setBackground(Color.black);
		Panel2.add(EntryGate);
			
			// 화폐 버튼
			JPanel panel_pay = new JPanel();
			panel_pay.setLayout(new BoxLayout(panel_pay, BoxLayout.PAGE_AXIS));
			panel_pay.setBackground(Color.orange);
				ImageIcon moneyicon = new ImageIcon("./image/화폐_1000.png");
				img=moneyicon.getImage();	img=img.getScaledInstance(300, 150, Image.SCALE_SMOOTH);
			JButton W1000= new JButton("1000", new ImageIcon(img));		// 1000원 버튼
			W1000.setBorderPainted(false);	W1000.setContentAreaFilled(false);	W1000.setForeground(Color.orange);
				moneyicon = new ImageIcon("./image/화폐_500.png");
				img=moneyicon.getImage();	img=img.getScaledInstance(130, 130, Image.SCALE_SMOOTH);
			JButton W500= new JButton("500", new ImageIcon(img));		// 500원 버튼
			W500.setBorderPainted(false);	W500.setContentAreaFilled(false);	W500.setForeground(Color.orange);
				moneyicon = new ImageIcon("./image/화폐_100.png");
				img=moneyicon.getImage();	img=img.getScaledInstance(110, 110, Image.SCALE_SMOOTH);
			JButton W100= new JButton("100", new ImageIcon(img));		// 100원 버튼
			W100.setBorderPainted(false);	W100.setContentAreaFilled(false);	W100.setForeground(Color.orange);
				moneyicon = new ImageIcon("./image/화폐_50.png");
				img=moneyicon.getImage();	img=img.getScaledInstance(90, 90, Image.SCALE_SMOOTH);
			JButton W50= new JButton("50", new ImageIcon(img));			// 50원 버튼
			W50.setBorderPainted(false);	W50.setContentAreaFilled(false);	W50.setForeground(Color.orange);
				moneyicon = new ImageIcon("./image/화폐_10.png");
				img=moneyicon.getImage();	img=img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
			JButton W10= new JButton("10", new ImageIcon(img));			// 10원 버튼
			W10.setBorderPainted(false);	W10.setContentAreaFilled(false);	W10.setForeground(Color.orange);
		
			panel_pay.add(W1000);	panel_pay.add(W500);	panel_pay.add(W100);	panel_pay.add(W50);	panel_pay.add(W10);
			Panel2.add(panel_pay);
			// 화폐 버튼 이벤트
			W1000.addActionListener(new MoneyButtonAction());
			W500.addActionListener(new MoneyButtonAction());
			W100.addActionListener(new MoneyButtonAction());
			W50.addActionListener(new MoneyButtonAction());
			W10.addActionListener(new MoneyButtonAction());
			
		mainPanel.add(Panel2);
		
		
		// 관리자 메뉴	
			JPanel panel_manager = new JPanel();
			panel_manager.setBackground(Color.orange);
			
				// 관리자 메뉴 아이콘
				ImageIcon menu_Managerimg = new ImageIcon("./image/메뉴_관리자(배경).png");
				img=menu_Managerimg.getImage();	img=img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
				menu_Managerimg.setImage(img);
				JButton Manager = new JButton(menu_Managerimg);		// 관리자 메뉴 버튼
				Manager.setBorderPainted(false);
				Manager.setContentAreaFilled(false);
				
				// 다이얼로그 (비밀번호 입력 -> 관리자 메뉴)
				Manager.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						password = new PW();
						JDialog checkpassword = new JDialog();	// 비밀번호 입력 다이얼로그
							checkpassword.setSize(300, 150);
							checkpassword.setLayout(new BorderLayout());
								checkpassword.add(new JPanel(), BorderLayout.NORTH);
							JPanel checkpassword_p = new JPanel();
								
								JLabel checkpassword_l = new JLabel("PW ");
								JPasswordField checkpassword_pf = new JPasswordField();
									checkpassword_l.setLabelFor(checkpassword_pf);
									checkpassword_pf.setPreferredSize(new Dimension(100, 25));
								JButton checkpassword_b = new JButton("Enter");
									checkpassword_b.setBackground(Color.orange);
									checkpassword_b.addActionListener(new ActionListener() {	// 비밀번호 확인 이벤트
										public void actionPerformed(ActionEvent e1) {
											// 비밀번호 확인
											if(password.checkPW(checkpassword_pf.getText())) {
											// true	
												ManagerFrame manager_frame = new ManagerFrame();	// 관리자 다이얼로그 set
												manager_frame.setVisible(true);
												checkpassword.setVisible(false);
											}
											else {
											// false
												JDialog errorinputpw = new JDialog(); 	// 잘못된 입력 안내 다이얼로그
													errorinputpw.setSize(200, 150);
													errorinputpw.setLayout(new BorderLayout());
													JLabel errorinputpw_l = new JLabel("잘못된 입력입니다.");
														errorinputpw_l.setFont(new Font("나눔스퀘어", Font.PLAIN, 15));
														errorinputpw_l.setBackground(Color.white);
														errorinputpw_l.setVerticalAlignment(SwingConstants.CENTER);
														errorinputpw_l.setHorizontalAlignment(SwingConstants.CENTER);
													JButton errorinputpw_b = new JButton("OK");
														errorinputpw_b.setBackground(Color.orange);
														errorinputpw_b.addActionListener(new ActionListener() {
															public void actionPerformed (ActionEvent e2) {
																errorinputpw.setVisible(false);
															}
														});
													errorinputpw.add(errorinputpw_l, BorderLayout.CENTER);
													errorinputpw.add(errorinputpw_b, BorderLayout.SOUTH);
												errorinputpw.setVisible(true);
											
											}
										}
									});
								checkpassword_p.add(checkpassword_l);
								checkpassword_p.add(checkpassword_pf);
							checkpassword.add(checkpassword_p, BorderLayout.CENTER);
							checkpassword.add(checkpassword_b, BorderLayout.SOUTH);
						checkpassword.setVisible(true);
						checkpassword.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					}
				});
				
				panel_manager.add(Manager);
			mainPanel.add(panel_manager);
			
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
// 음료 메뉴 버튼 클릭
	class DrinkButtonAction implements ActionListener {
		void subMoney(int price) {
			pay.total-=price;		// 지불 금액에서 가격만큼 차감
		}
		public void actionPerformed (ActionEvent e) {	// 이벤트 메소드
			JButton b = (JButton)e.getSource();
			boolean check=false;
			// 해당 음료 계산
			for(int i=0;i<drinkButton.length;i++) {
				if(b.getText().equals(drinkButton[i].getText())) {
					if(drink[i].now==drink[i].state1) {
						check=true;
						subMoney(drink[i].price);	// 해당 음료 금액만큼 차감
						drink[i].amount--;			// 해당 음료 재고--
						leftPayText.setText(" "+pay.total+" ");		// 금액창 내용 변경
						Take.setIcon(TakeDrink[i]);					// 배출구 이미지 변경
						break;
					}
				}
			}
			
			// 음료 메뉴 버튼 상태 변경
			for(int i=0;i<drinkButton.length;i++) {
				if(drink[i].amount>0) {
					if(drink[i].price<=pay.total)	drink[i].now=drink[i].state1;
					else	drink[i].now=drink[i].state2;
				}
				else	drink[i].now=drink[i].state3;	
				drinkButton[i].setIcon(drink[i].now);
			}
			
			// 음료 구매 사항 파일에 추가
			String [] data = {"0", "0", "0", "0", "0", "0", "0"};
			String [] data2 = {"0", "0", "0"};	// 날짜 | 총매출 | 판매수
			SimpleDateFormat sdf1 = new SimpleDateFormat("yy/MM/dd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yy/MM");
			Date d = new Date();
			for(int i=0;i<drink.length;i++) {
				if(b.getText().equals(drinkButton[i].getText())) {	// 클릭된 버튼 찾기 -> 인덱스 i
					// 파일 마지막 줄 읽기 -> 당일 또는 전날
					try{	////////////////////////////////////////////////// 일별.txt
						File file = new File("./매출_일별.txt");	
						FileReader fr = new FileReader(file);
						FileWriter fw = new FileWriter(file, true);
						Scanner scanner = new Scanner(fr);
						
						while(scanner.hasNextLine()) {
							for(int a=0;a<7;a++)
								data[a]=scanner.next();	// 마지막 줄 저장
						}
						
						if(data[0].equals(sdf1.format(d))) {	//  오늘 날짜 존재
							// 총매출 + 메뉴 가격
							int p = Integer.parseInt(data[1])+drink[i].price;
							data[1]=Integer.toString(p);
							// 해당 메뉴 판매수 +1
							p = Integer.parseInt(data[i+2])+1;	//data[i+2]==drink[i]
							data[i+2]=Integer.toString(p);
							
							// 파일 마지막줄 remove -> 변경된 data 입력
							RandomAccessFile raf = new RandomAccessFile(file,"rw");
							long pos = file.length()-1;	// 뒤로부터
							while(true) {		// 마지막 줄 앞부분까지 pos 이동
								raf.seek(pos);
								if(raf.readByte()=='\n' || pos==0)	break;
								pos--;
							}
							if(pos==0)	raf.seek(0);							
							for(int k=0;k<data.length;k++) {
								raf.writeBytes(data[k]);
								if(k!=data.length-1)	raf.writeBytes(" ");
							}
							raf.close();
						}
						else {	// 오늘 날짜 존재 X
							data[0]=sdf1.format(d).toString();	// 오늘 날짜 저장
							for(int n=1;n<7;n++)
								data[n]= "0";		// 0으로 리셋
							data[1]=Integer.toString(drink[i].price);	// 처음 판매된 메뉴 가격 = 총매출
							data[i+2]="1";								// 해당 음료가 판매된 수=1
							
							// 파일 마지막줄에 data 입력
							if(file.length()!=0)		// 파일에 내용 있음.
								fw.write("\n");
							for(int m=0;m<data.length;m++) {
								fw.write(data[m]); 			// data[] 문자열 + 띄어쓰기 저장
								if(m!=data.length-1)	fw.write(" ");
							}
						}
						fr.close();
						fw.close();
						scanner.close();
					}
					catch(IOException e1) {		// 매출_일별 파일 없음
						new ExceptionDialog("file not found! :: 매출_일별.txt");
					}
					
					try{	////////////////////////////////////////////////// 일별i.txt
						String fileName = new String("./매출_일별"+i+".txt");
						File file = new File(fileName);	
						FileReader fr = new FileReader(file);
						FileWriter fw = new FileWriter(file, true);
						Scanner scanner = new Scanner(fr);
						
						while(scanner.hasNextLine()) {
							for(int a=0;a<3;a++)
								data2[a]=scanner.next();	// 마지막 줄 저장
						}
						
						if(data2[0].equals(sdf1.format(d))) {	//  오늘 날짜 존재
							// 총매출 + 메뉴 가격
							int p = Integer.parseInt(data2[1])+drink[i].price;	// data[1](촘매출)
							data2[1]=Integer.toString(p);
							// 해당 메뉴 판매수 +1
							p = Integer.parseInt(data2[2])+1;	//data[2](판매수)==drink[i]
							data2[2]=Integer.toString(p);
							
							// 파일 마지막줄 remove -> 변경된 data 입력
							RandomAccessFile raf = new RandomAccessFile(file,"rw");
							long pos = file.length()-1;	// 뒤로부터
							while(true) {		// 마지막 줄 앞부분까지 pos 이동
								raf.seek(pos);
								if(raf.readByte()=='\n' || pos==0)	break;
								pos--;
							}
							if(pos==0)	raf.seek(0);							
							for(int k=0;k<data2.length;k++) {
								raf.writeBytes(data2[k]);
								if(k!=data2.length-1)	raf.writeBytes(" ");
							}
							raf.close();
						}
						else {	// 오늘 날짜 존재 X
							data2[0]=sdf1.format(d).toString();	// 오늘 날짜 저장
							data2[1]=Integer.toString(drink[i].price);	// 처음 판매된 메뉴 가격 = 총매출
							data2[2]="1";								// 해당 음료가 판매된 수=1
							
							// 파일 마지막줄에 data 입력
							if(file.length()!=0)		// 파일에 내용 있음.
								fw.write("\n");
							for(int m=0;m<data2.length;m++) {
								fw.write(data2[m]); 			// data[] 문자열 + 띄어쓰기 저장
								if(m!=data2.length-1)	fw.write(" ");
							}
						}
						fr.close();
						fw.close();
						scanner.close();
					}
					catch(IOException e1) {	// 매출_일별i 파일 없음
						new ExceptionDialog("file not found! :: 매출_일별"+i+".txt");
					}
					
					try {	////////////////////////////////////////////////// 월별.txt
						File file = new File("./매출_월별.txt");
						FileReader fr = new FileReader(file);
						FileWriter fw = new FileWriter(file, true);
						Scanner scanner = new Scanner(fr);
						while(scanner.hasNextLine()) {
							for(int a=0;a<7;a++)
								data[a]=scanner.next();	// 마지막 줄 저장
						}
						
						if(data[0].equals(sdf2.format(d))) {		// 해당 날짜 존재
							int p = Integer.parseInt(data[1])+drink[i].price;	// 총매출
							data[1]=Integer.toString(p);
							p = Integer.parseInt(data[i+2])+1;					// 음료 판매+1
							data[i+2]=Integer.toString(p);
							
							RandomAccessFile raf = new RandomAccessFile(file, "rw");
							long pos = file.length()-1;
							while(true) {
								raf.seek(pos);
								if(raf.readByte()=='\n' || pos==0) break;
								pos--;
							}
							if(pos==0)	raf.seek(0);	
							for(int k=0;k<data.length;k++) {
								raf.writeBytes(data[k]);
								if(k!=data.length-1)	raf.writeBytes(" ");
							}
							raf.close();
						}
						else {	// 오늘 날짜 존재 X
							data[0]=sdf2.format(d).toString();	// 오늘 날짜 저장
							for(int n=1;n<7;n++)
								data[n]= "0";		// 0으로 리셋
							data[1]=Integer.toString(drink[i].price);	// 처음 판매된 메뉴 가격 = 총매출
							data[i+2]="1";								// 해당 음료가 판매된 수=1
							
							// 파일 마지막줄에 data 입력
							if(file.length()!=0)		// 파일에 내용 있음.
								fw.write("\n");
							for(int m=0;m<data.length;m++) {
								fw.write(data[m]); 			// data[] 문자열 + 띄어쓰기 저장
								if(m!=data.length-1)	fw.write(" ");
							}
						}
						fr.close();
						fw.close();
						scanner.close();     
					}
					catch(IOException e1) {		// 매출_월별 파일없음
						new ExceptionDialog("file not found! :: 매출_월별.txt");
					}
					

					try {	////////////////////////////////////////////////// 월별i.txt
						String fileName = new String("./매출_월별"+i+".txt");
						File file = new File(fileName);
						FileReader fr = new FileReader(file);
						FileWriter fw = new FileWriter(file, true);
						Scanner scanner = new Scanner(fr);
						while(scanner.hasNextLine()) {
							for(int a=0;a<3;a++)
								data2[a]=scanner.next();	// 마지막 줄 저장
						}
						
						if(data2[0].equals(sdf2.format(d))) {		// 해당 날짜 존재
							int p = Integer.parseInt(data2[1])+drink[i].price;	// 총매출
							data2[1]=Integer.toString(p);
							p = Integer.parseInt(data2[2])+1;	// 판매수+1
							data2[2]=Integer.toString(p);
							
							RandomAccessFile raf = new RandomAccessFile(file, "rw");
							long pos = file.length()-1;
							while(true) {
								raf.seek(pos);
								if(raf.readByte()=='\n' || pos==0) break;
								pos--;
							}
							if(pos==0)	raf.seek(0);	
							for(int k=0;k<data2.length;k++) {
								raf.writeBytes(data2[k]);
								if(k!=data2.length-1)	raf.writeBytes(" ");
							}
							raf.close();
						}
						else {	// 오늘 날짜 존재 X
							data2[0]=sdf2.format(d).toString();	// 오늘 날짜 저장
							data2[1]=Integer.toString(drink[i].price);	// 처음 판매된 메뉴 가격 = 총매출
							data2[2]="1";								// 해당 음료가 판매된 수=1
							
							// 파일 마지막줄에 data 입력
							if(file.length()!=0)		// 파일에 내용 있음.
								fw.write("\n");
							for(int m=0;m<data2.length;m++) {
								fw.write(data2[m]); 			// data[] 문자열 + 띄어쓰기 저장
								if(m!=data2.length-1)	fw.write(" ");
							}
						}
						fr.close();
						fw.close();
						scanner.close();     
					}
					catch(IOException e1) {		// 매출_월별i 파일 없음
						new ExceptionDialog("file not found! :: 매출_월별"+i+".txt");
					}
					
					try {	// 재고 파일에 변경된 재고 사항 입력
						File file = new File("./재고.txt");
						FileWriter fw = new FileWriter(file, false);
						String dataDrink = new String(drink[0].amount+" "+drink[1].amount+" "+drink[2].amount+" "+drink[3].amount+" "+drink[4].amount);
						fw.write(dataDrink);
						fw.close();
					}
					catch(IOException eq) {		// 재고 파일 없음
						new ExceptionDialog("file not found! :: 재고.txt");
					}
					
					// 재고 소진 파일
					if(drink[i].amount==0&&b.getText().equals(drink[i].name)&&check) {		// 재고 소진되면 날짜 / 재고 소진된 음료 내용 추가
						try{
							File f = new File("./재고소진.txt");
							FileWriter fw = new FileWriter(f, true);
							String [] text = new String[2];
							text[0]=sdf1.format(d);		text[1]=drink[i].name;
							if(f.length()!=0)	fw.write("\n");
							fw.write(text[0]+" ");
							fw.write(text[1]);
							fw.close();
						}
						catch(IOException e1) {		// 재고소진 파일 없음
							new ExceptionDialog("file not found! :: 재고소진.txt");
						}
					}
					break;
				}				
			}
		}
	}
	
	
// 레버 버튼 클릭
	class changeLeverAction implements ActionListener{
		public void actionPerformed (ActionEvent e) {
			Money change = new Money(); 		// 잔돈		
			boolean lack = false;
			//잔돈 계산
			change.W1000=pay.total/1000;	pay.total-=1000*change.W1000;	
			change.W500=pay.total/500;		pay.total-=change.W500*500;
			change.W100=pay.total/100;		pay.total-=change.W100*100;
			change.W50=pay.total/50;		pay.total-=change.W50*50;
			change.W10=pay.total/10;		pay.total-=change.W10*10;	
			
			if(VMoney.W1000<change.W1000) {
				change.W1000=VMoney.W1000;	VMoney.W1000=0;
				lack = true;	// 잔돈 부족
			}
			else {
				VMoney.W1000 -= change.W1000;
			}
			
			if(VMoney.W500<change.W500) {
				change.W500=VMoney.W500;	VMoney.W500=0;
				lack = true;	// 잔돈 부족
			}	
			else {
				VMoney.W500 -= change.W500;
			}
			
			if(VMoney.W100<change.W100) {
				change.W100=VMoney.W100;	VMoney.W100=0;
				lack = true;	// 잔돈 부족
			}
			else {
				VMoney.W100 -= change.W100;
			}
			
			if(VMoney.W50<change.W50) {
				change.W50=VMoney.W50;	VMoney.W50=0;
				lack = true;	// 잔돈 부족
			}
			else {
				VMoney.W50 -= change.W50;
			}
			
			if(VMoney.W10<change.W10) {
				change.W10=VMoney.W10;	VMoney.W10=0;
				lack = true;	// 잔돈 부족
			}
			else {
				VMoney.W10 -= change.W10;
			}
			
			try{	// 자판기 내의 변경된 화폐 상황을 파일에 입력
				File f = new File("./화폐.txt");
				FileReader fr = new FileReader(f);
				FileWriter fw = new FileWriter(f);
				Scanner scanner = new Scanner(fr);
				String data[] = new String[5];
				int i=0;
				while(scanner.hasNext()) {
					data[i++]=scanner.next();
				}
				data[0]=Integer.toString(VMoney.W1000);
				data[1]=Integer.toString(VMoney.W500);
				data[2]=Integer.toString(VMoney.W100);
				data[3]=Integer.toString(VMoney.W50);
				data[4]=Integer.toString(VMoney.W10);
				String put = new String(data[0]+" "+data[1]+" "+data[2]+" "+data[3]+" "+data[4]);
				fw.write(put);
				fr.close();		fw.close(); 	scanner.close();
				
			}
			catch(IOException e1) {		// 화폐 파일 없음
				new ExceptionDialog("file not found! :: 화폐.txt");
			}
						
			// 팝업창
			JDialog changePop = new JDialog();
			changePop.setLayout(new BorderLayout());
			changePop.setSize(400, 600);
			changePop.setBackground(Color.orange);
				// 잔돈 화폐 출력
				JPanel putMoney = new JPanel();
				putMoney.setLayout(new BoxLayout(putMoney, BoxLayout.Y_AXIS));
				putMoney.setBackground(Color.orange);
				if(change.W1000>0) {
					JPanel panel_1000 = new JPanel();
					panel_1000.setBackground(Color.orange);
					panel_1000.setLayout(new FlowLayout());
					ImageIcon imgicon = new ImageIcon("./image/화폐_1000.png");
					Image img=imgicon.getImage();	img=img.getScaledInstance(200, 100, Image.SCALE_SMOOTH);
					imgicon.setImage(img);
					JLabel changeImage1000 = new JLabel(imgicon);
					JLabel change_1000 = new JLabel(" × "+change.W1000);
					panel_1000.add(changeImage1000);
					panel_1000.add(change_1000);
					putMoney.add(panel_1000);
				}
				if(change.W500>0) {
					JPanel panel_500 = new JPanel();
					panel_500.setBackground(Color.orange);
					panel_500.setLayout(new FlowLayout());
					ImageIcon imgicon = new ImageIcon("./image/화폐_500.png");
					Image img=imgicon.getImage();	img=img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
					imgicon.setImage(img);
					JLabel changeImage500 = new JLabel(imgicon);
					JLabel change_500 = new JLabel(" × "+change.W500);
					panel_500.add(changeImage500);
					panel_500.add(change_500);
					putMoney.add(panel_500);
				}
				if(change.W100>0) {
					JPanel panel_100 = new JPanel();
					panel_100.setBackground(Color.orange);
					panel_100.setLayout(new FlowLayout());
					ImageIcon imgicon = new ImageIcon("./image/화폐_100.png");
					Image img=imgicon.getImage();	img=img.getScaledInstance(90, 90, Image.SCALE_SMOOTH);
					imgicon.setImage(img);
					JLabel changeImage100 = new JLabel(imgicon);
					JLabel change_100 = new JLabel(" × "+change.W100);
					panel_100.add(changeImage100);
					panel_100.add(change_100);
					putMoney.add(panel_100);
				}
				if(change.W50>0) {
					JPanel panel_50 = new JPanel();
					panel_50.setBackground(Color.orange);
					panel_50.setLayout(new FlowLayout());
					ImageIcon imgicon = new ImageIcon("./image/화폐_50.png");
					Image img=imgicon.getImage();	img=img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
					imgicon.setImage(img);
					JLabel changeImage50 = new JLabel(imgicon);
					JLabel change_50 = new JLabel(" × "+change.W50);
					panel_50.add(changeImage50);
					panel_50.add(change_50);
					putMoney.add(panel_50);
				}
				if(change.W10>0) {
					JPanel panel_10 = new JPanel();
					panel_10.setBackground(Color.orange);
					panel_10.setLayout(new FlowLayout());
					ImageIcon imgicon = new ImageIcon("./image/화폐_10.png");
					Image img=imgicon.getImage();	img=img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
					imgicon.setImage(img);
					JLabel changeImage10 = new JLabel(imgicon);
					JLabel change_10 = new JLabel(" × "+change.W10);
					panel_10.add(changeImage10);
					panel_10.add(change_10);
					putMoney.add(panel_10);
				}
		
				if(lack==true) {
					JLabel informLack = new JLabel("자판기 내의 잔돈이 부족합니다.");
					informLack.setFont(new Font("나눔스퀘어", Font.PLAIN, 15));
					informLack.setBackground(Color.orange);
					informLack.setHorizontalAlignment(SwingConstants.CENTER);
					putMoney.add(informLack);
				}
				changePop.add(putMoney);
				
				// 확인 버튼
				JButton okbutton = new JButton("OK");
				okbutton.setBackground(Color.white);
				okbutton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						changePop.setVisible(false);
					}
				});
				changePop.add(okbutton, BorderLayout.SOUTH);
			changePop.setVisible(true);
			changePop.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			// 지불금액 초기화
			pay = new Pay();
			leftPayText.setText(" "+pay.total+" ");
			for(int i=0;i<drink.length;i++){
				if(drink[i].amount>0)	drink[i].now=drink[i].state2;
				else	drink[i].now=drink[i].state3;
				drinkButton[i].setIcon(drink[i].now);
			}
			Take.setIcon(TakeDrink[5]);		// 기본배출구그림
		}
	}
	
// 화폐 버튼 클릭
	class MoneyButtonAction implements ActionListener{
		public void actionPerformed (ActionEvent e) {
			JButton b = (JButton)e.getSource();
			
			// 화폐 선택 -> 자판기로 이동
			if(b.getText().equals("1000")) {
				VMoney.W1000++;
				if(pay.total>=3000 && pay.payCoin==false)	VMoney.W1000--;	// 지폐 최대 3장
				else if(pay.total>=5000)		VMoney.W1000--;
				else pay.total+=1000;
			}
			else if(b.getText().equals("500")) {
				VMoney.W500++;		pay.payCoin=true;
				if(pay.total>=5000)		VMoney.W500--;
				else pay.total+=500;
			}
			else if(b.getText().equals("100")) {
				VMoney.W100++;		pay.payCoin=true;
				if(pay.total>=5000)		VMoney.W100--;
				else pay.total+=100;
			}
			else if(b.getText().equals("50")) {
				VMoney.W50++;		pay.payCoin=true;
				if(pay.total>=5000)		VMoney.W50--;
				else pay.total+=50;
			}
			else if(b.getText().equals("10")) {
				VMoney.W10++;		pay.payCoin=true;
				if(pay.total>=5000)		VMoney.W10--;
				else pay.total+=10;
			}
			
			// 메뉴 그림 활성화
			for(int i=0;i<drink.length;i++) {
				if(drink[i].amount>0) {
					if(drink[i].price<=pay.total)	drink[i].now=drink[i].state1;
					else	drink[i].now=drink[i].state2;
				}
				else drink[i].now=drink[i].state3;
				drinkButton[i].setIcon(drink[i].now);				
			}
			leftPayText.setText(" "+pay.total+" ");
		}
	}

// 관리자 버튼 클릭 → 새로운 다이얼로그
	public class ManagerFrame extends JFrame{
		Container c = getContentPane();
		CardLayout cd=null;
		JPanel [] Menu = new JPanel[6];	// 관리자(5), 재고관리(0), 화폐관리(1), 메뉴설정(2), 매출산출(3), 비밀번호(4)
		JButton [] MenuButton = new JButton[5];
		String [] menu_name = {"재고관리", "화폐관리", "메뉴설정", "매출산출", "비밀번호", "관리자"};
		
		int i;
		
		public ManagerFrame() {
			this.setTitle("관리자");
			this.setSize(500, 800);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	// X -> 현재창 닫기
			
	
			JPanel cardBox = new JPanel();	
			cd = new CardLayout();		// 관리자 메뉴 CardLayout
			cardBox.setLayout(cd);
			
			for(i=0;i<Menu.length;i++) 
				Menu[i]=new JPanel();
		
			Menu[5].setLayout(new GridLayout(3,2,0,0));
			Menu[5].setBackground(Color.orange);
			
			// 메뉴 아이콘
				MenuButton[0]=new JButton();	MenuButton[0].setText("재고 관리");
					ImageIcon menuimg = new ImageIcon("./image/관리자메뉴_재고관리.png");
					img=menuimg.getImage();	img=img.getScaledInstance(180, 200, Image.SCALE_SMOOTH);
					menuimg.setImage(img);	MenuButton[0].setIcon(menuimg); 
				MenuButton[1]=new JButton();	MenuButton[1].setText("화폐 관리");
					menuimg = new ImageIcon("./image/관리자메뉴_화폐관리.png");
					img=menuimg.getImage();	img=img.getScaledInstance(210, 200, Image.SCALE_SMOOTH);
					menuimg.setImage(img);	MenuButton[1].setIcon(menuimg); 
				MenuButton[2]=new JButton();	MenuButton[2].setText("메뉴관리");
					menuimg = new ImageIcon("./image/관리자메뉴_메뉴관리.png");
					img=menuimg.getImage();	img=img.getScaledInstance(210, 200, Image.SCALE_SMOOTH);
					menuimg.setImage(img);	MenuButton[2].setIcon(menuimg);
				MenuButton[3]=new JButton();	MenuButton[3].setText("매출산출");
					menuimg = new ImageIcon("./image/관리자메뉴_매출산출.png");
					img=menuimg.getImage();	img=img.getScaledInstance(210, 200, Image.SCALE_SMOOTH);
					menuimg.setImage(img);	MenuButton[3].setIcon(menuimg);
				MenuButton[4]=new JButton();	MenuButton[4].setText("비밀번호 설정");
					menuimg = new ImageIcon("./image/관리자메뉴_비밀번호.png");
					img=menuimg.getImage();	img=img.getScaledInstance(210, 200, Image.SCALE_SMOOTH);
					menuimg.setImage(img);	MenuButton[4].setIcon(menuimg);
			
				for(i=0;i<MenuButton.length;i++) {
					if(i!=1 && i!=2)MenuButton[i].setBackground(Color.white);
					else MenuButton[i].setBackground(Color.orange);
					MenuButton[i].setFont(new Font("나눔스퀘어라운드 ExtraBold",Font.PLAIN, 25));
					MenuButton[i].setHorizontalTextPosition(SwingConstants.CENTER);
					MenuButton[i].setVerticalTextPosition(SwingConstants.NORTH);
					MenuButton[i].addActionListener(new ActionListener() {		// 메뉴 버튼 클릭시 해당 메뉴 패널로 변경
						public void actionPerformed (ActionEvent e) {
							JButton b = (JButton)e.getSource();
							for(i=0;i<MenuButton.length;i++) {
								if(b.getText().equals(MenuButton[i].getText())) {
									cd.show(cardBox, menu_name[i]);
									break;
								}
							}
						}
					});
					Menu[5].add(MenuButton[i]);		// 관리자 메뉴에 해당 버튼을 추가
				}
				cardBox.add(Menu[5], "관리자");			
			

			// Menu0 - 재고 관리
			JPanel panel_back0 = new JPanel();	// 뒤로가기
				panel_back0.setBackground(Color.orange);
				ImageIcon BackIcon = new ImageIcon("./image/back(orange).png");
				img=BackIcon.getImage();	img=img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
				BackIcon.setImage(img);
				JButton Back0 = new JButton(BackIcon);		// 뒤로가기 버튼. Menu5 제외 모든 패널에 적용
				Back0.setBorderPainted(false); 	Back0.setFocusPainted(false);	Back0.setBackground(Color.orange);		
				Back0.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cd.show(cardBox, menu_name[5]);					
					}
				});
				panel_back0.add(Back0);
				
			Menu[0].setLayout(new BorderLayout());	// Menu0 레이아웃
			JPanel M0_System = new JPanel();		// 메뉴 시스템 패널
			M0_System.setLayout(new BoxLayout(M0_System, BoxLayout.PAGE_AXIS));
				JPanel [] list0_panel = new JPanel[drink.length];
				JLabel [] list0_name = new JLabel[drink.length];		// 리스트 이름 배열
				JLabel [] list0_price = new JLabel[drink.length];	// 리스트 가격 배열
				SpinnerNumberModel [] M0_SNM = new SpinnerNumberModel[drink.length];	// 스피너 모델
				JSpinner [] list0_spinner = new JSpinner[drink.length];				// 스피너 - 재고 수 관리
				for(i=0;i<drink.length;i++) {
					list0_panel[i]=new JPanel();
					list0_panel[i].setPreferredSize(new Dimension(400, 20));
					list0_panel[i].setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
					list0_panel[i].setBackground(Color.white);
					list0_name[i]=new JLabel(drink[i].name);	list0_price[i]=new JLabel(drink[i].price+" W");
					list0_name[i].setFont(new Font("나눔스퀘어", Font.PLAIN, 20));		list0_name[i].setPreferredSize(new Dimension(100, 20));
					list0_price[i].setFont(new Font("나눔스퀘어", Font.PLAIN, 20));	list0_price[i].setPreferredSize(new Dimension(100, 20));
					M0_SNM[i] = new SpinnerNumberModel(drink[i].amount,0,100, 1);
					list0_spinner[i] = new JSpinner(M0_SNM[i]);
					list0_spinner[i].setEditor(new JSpinner.DefaultEditor(list0_spinner[i]));
					list0_spinner[i].setPreferredSize(new Dimension(50, 30));
					list0_spinner[i].addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent e) {		// 스피너 화살표로 재고 관리
							int value=(int)((JSpinner)e.getSource()).getValue();
							for(i=0;i<drink.length;i++) {
								if(e.getSource().equals(list0_spinner[i])) {
									drink[i].amount=value;		// 변동된 값 저장
									if(drink[i].amount>0) {	// 재고 있음
										if(pay.total>=drink[i].price)	drink[i].now=drink[i].state1;	// 금액 충족
										else							drink[i].now=drink[i].state2;	// 금액 부족
									}
									else	drink[i].now=drink[i].state3;	// 재고 없음
									drinkButton[i].setIcon(drink[i].now); 	// 메뉴 버튼 상태 변경
									break;
								}
							}	
							// 재고.txt 변경
							try{
								File file = new File("./재고.txt");
								FileWriter fw = new FileWriter(file, false);
								String data = new String(drink[0].amount+" "+drink[1].amount+" "+drink[2].amount
										+" "+drink[3].amount+" "+drink[4].amount);
								fw.write(data);
								fw.close();
							}
							catch(IOException e1) {		// 재고 파일 없음
								new ExceptionDialog("file not found! :: 재고.txt");
							}
							
						}
					});
					
					list0_panel[i].add(list0_name[i]);	list0_panel[i].add(list0_price[i]);
					list0_panel[i].add(list0_spinner[i]);
					M0_System.add(list0_panel[i]);
				}
				
				Menu[0].add(panel_back0, BorderLayout.NORTH);	// 뒤로 가기 버튼
				Menu[0].add(M0_System, BorderLayout.CENTER);	// 시스템 패널
			cardBox.add(Menu[0], "재고관리");	// 카드패널에 해당 메뉴 추가
			
			
			// Menu1 - 화폐관리
			JPanel panel_back1 = new JPanel();	// 뒤로가기
				panel_back1.setBackground(Color.orange);
				BackIcon = new ImageIcon("./image/back(orange).png");
				img=BackIcon.getImage();	img=img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
				BackIcon.setImage(img);
				JButton Back1 = new JButton(BackIcon);		// 뒤로가기 버튼. Menu5 제외 모든 패널에 적용
					Back1.setBorderPainted(false); 	Back1.setFocusPainted(false);	Back1.setBackground(Color.orange);		
					Back1.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							cd.show(cardBox, menu_name[5]);					
						}
					});
				
			Menu[1].setLayout(new BorderLayout());
			JPanel M1_System = new JPanel();
			M1_System.setLayout(new BoxLayout(M1_System, BoxLayout.PAGE_AXIS));
				JPanel [] list1_panel = new JPanel[5];	// 화폐수
				JLabel [] list1_img = new JLabel[5];	// 이미지 설정
					ImageIcon imgicon = new ImageIcon("./image/화폐_1000(white).png");	img=imgicon.getImage();
					img=img.getScaledInstance(200, 100, Image.SCALE_SMOOTH);			imgicon.setImage(img);
					list1_img[0] = new JLabel();	list1_img[0].setIcon(imgicon);
					imgicon = new ImageIcon("./image/화폐_500(white).png");				img=imgicon.getImage();
					img=img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);			imgicon.setImage(img);
					list1_img[1] = new JLabel();	list1_img[1].setIcon(imgicon);
					imgicon = new ImageIcon("./image/화폐_100(white).png");				img=imgicon.getImage();
					img=img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);			imgicon.setImage(img);
					list1_img[2] = new JLabel();	list1_img[2].setIcon(imgicon);
					imgicon = new ImageIcon("./image/화폐_50(white).png");				img=imgicon.getImage();
					img=img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);			imgicon.setImage(img);
					list1_img[3] = new JLabel();	list1_img[3].setIcon(imgicon);
					imgicon = new ImageIcon("./image/화폐_10(white).png");				img=imgicon.getImage();
					img=img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);			imgicon.setImage(img);
					list1_img[4] = new JLabel();	list1_img[4].setIcon(imgicon);					
				SpinnerNumberModel [] M1_SNM = new SpinnerNumberModel[5];
					M1_SNM[0] = new SpinnerNumberModel(VMoney.W1000, 0, 100, 1);
					M1_SNM[1] = new SpinnerNumberModel(VMoney.W500, 0, 100, 1);
					M1_SNM[2] = new SpinnerNumberModel(VMoney.W100, 0, 100, 1);
					M1_SNM[3] = new SpinnerNumberModel(VMoney.W50, 0, 100, 1);
					M1_SNM[4] = new SpinnerNumberModel(VMoney.W10, 0, 100, 1);
				JSpinner [] list1_spinner = new JSpinner[5];		// 스피너로 화폐 수 관리
				for(i=0;i<5;i++) {
					list1_panel[i] = new JPanel();
					list1_panel[i].setLayout(new FlowLayout(FlowLayout.CENTER, 50, 10));
					list1_panel[i].setBackground(Color.white);
					list1_spinner[i] = new JSpinner(M1_SNM[i]);
					list1_spinner[i].setEditor(new JSpinner.DefaultEditor(list1_spinner[i]));
					list1_spinner[i].setPreferredSize(new Dimension(50, 30));
					list1_spinner[i].addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent e) {	// 스피너 이벤트
							int value =(int)((JSpinner)e.getSource()).getValue();
							if(e.getSource().equals(list1_spinner[0]))		VMoney.W1000=value;	// 화폐수 값 변경
							else if(e.getSource().equals(list1_spinner[1]))	VMoney.W500=value;
							else if(e.getSource().equals(list1_spinner[2]))	VMoney.W100=value;
							else if(e.getSource().equals(list1_spinner[3]))	VMoney.W50=value;
							else if(e.getSource().equals(list1_spinner[4]))	VMoney.W10=value;
							
							try{	// 변경된 화폐수 파일에 입력
								File f = new File("./화폐.txt");
								FileWriter fw = new FileWriter(f);
								String put = new String(VMoney.W1000+" "+VMoney.W500+" "
								                       +VMoney.W100+" "+VMoney.W50+" "+VMoney.W10);
								fw.write(put);
								fw.close();
							}
							catch(IOException e2) {		// 화폐 파일 없음
								new ExceptionDialog("file not found! :: 화폐.txt");
							}
						}
					});
					
					list1_panel[i].add(list1_img[i], BorderLayout.WEST);
					list1_panel[i].add(list1_spinner[i], BorderLayout.EAST);
					M1_System.add(list1_panel[i]);
				}
				
				// 수금 이벤트
					ImageIcon getMoneyicon=new ImageIcon("./image/수금.png");		img=getMoneyicon.getImage();
					img=img.getScaledInstance(50, 55, Image.SCALE_SMOOTH);		getMoneyicon.setImage(img);
				JButton getMoneyButton = new JButton(getMoneyicon);		// 수금 버튼
					getMoneyButton.setBackground(Color.orange);
					getMoneyButton.setBorderPainted(false); 	getMoneyButton.setFocusPainted(false);
					getMoneyButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {	// 수금 버튼 이벤트
							Money g = new Money();
							if(VMoney.W1000>BasisMoney)	{			// 자판기 내의 화폐를 BasisMoney만큼 남김
								g.W1000 = VMoney.W1000-BasisMoney;
								VMoney.W1000=BasisMoney;
								list1_spinner[0].setValue(VMoney.W1000);
							}
							else {
								g.W1000=0;
							}
							
							if(VMoney.W500>BasisMoney)	{
								g.W500 = VMoney.W500-BasisMoney;
								VMoney.W500=BasisMoney;
								list1_spinner[1].setValue(VMoney.W500);
							}
							else {
								g.W500=0;
							}
							
							if(VMoney.W100>BasisMoney)	{
								g.W100 = VMoney.W100-BasisMoney;
								VMoney.W100=BasisMoney;
								list1_spinner[2].setValue(VMoney.W100);
							}
							else {
								g.W100=0;
							}
							
							if(VMoney.W50>BasisMoney)	{
								g.W50 = VMoney.W50-BasisMoney;
								VMoney.W50=BasisMoney;
								list1_spinner[3].setValue(VMoney.W50);
							}
							else {
								g.W50=0;
							}
							
							if(VMoney.W10>BasisMoney)	{
								g.W10 = VMoney.W10-BasisMoney;
								VMoney.W10=BasisMoney;
								list1_spinner[4].setValue(VMoney.W10);
							}
							else {
								g.W10=0;
							}
							
							JDialog gd = new JDialog();		// 수금 안내 다이얼로그, 총 수금 금액 출력
								gd.setTitle("수금 안내");	gd.setSize(300, 100);
								gd.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
								gd.setLayout(new BorderLayout());
								JPanel gp = new JPanel();
									gp.setBackground(Color.white);
								JLabel gl = new JLabel("총 "+g.getTotal()+" 원 수금");
									gl.setHorizontalTextPosition(SwingConstants.CENTER);
									gl.setFont(new Font("나눔스퀘어", Font.BOLD, 20));
								JButton gb = new JButton("OK");
									gb.setBackground(Color.orange); 
									gb.addActionListener(new ActionListener() {
										public void actionPerformed (ActionEvent e) {
											gd.setVisible(false);
										}
									});
									gp.add(gl);
								gd.add(gp, BorderLayout.CENTER);
								gd.add(gb, BorderLayout.SOUTH);
								gd.setVisible(true);	
								gd.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
								
								try{	// 변경된 화폐수 파일에 입력
									File f = new File("./화폐.txt");
									FileWriter fw = new FileWriter(f);
									String put = new String(VMoney.W1000+" "+VMoney.W500+" "
									                +VMoney.W100+" "+VMoney.W50+" "+VMoney.W10);
									fw.write(put);
									fw.close();
								}
								catch(IOException e2) {		// 화폐 파일 없음
									new ExceptionDialog("file not found! :: 화폐.txt");
								}
						}
					});
					
					panel_back1.add(getMoneyButton);
					panel_back1.add(Back1);
				
				Menu[1].add(panel_back1, BorderLayout.NORTH);
				Menu[1].add(M1_System, BorderLayout.CENTER);
			cardBox.add(Menu[1], "화폐관리");
			
			// Menu2 - 메뉴 설정
			Menu[2].setLayout(new BorderLayout());
			JPanel panel_back2 = new JPanel();	// 뒤로가기
				panel_back2.setBackground(Color.orange);
				BackIcon = new ImageIcon("./image/back(orange).png");
				img=BackIcon.getImage();	img=img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
				BackIcon.setImage(img);
				JButton Back2 = new JButton(BackIcon);		// 뒤로가기 버튼. Menu5 제외 모든 패널에 적용
				Back2.setBorderPainted(false); 	Back2.setFocusPainted(false);	Back2.setBackground(Color.orange);		
				Back2.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cd.show(cardBox, menu_name[5]);					
					}
				});
				panel_back2.add(Back2);
				
				JPanel M2_System = new JPanel();
					M2_System.setBackground(Color.white);
					M2_System.setLayout(new BoxLayout(M2_System, BoxLayout.PAGE_AXIS));
					JPanel[] list2_panel = new JPanel[drink.length];
					JLabel[] drinkimg = new JLabel[drink.length];
					JTextField [] list2_name = new JTextField[drink.length];		// 음료 이름 textfield
					JTextField [] list2_price = new JTextField[drink.length];		// 음료 가격 textfield
					
					
					for(i=0;i<drink.length;i++) {
						list2_panel[i] = new JPanel();
						list2_panel[i].setLayout(new FlowLayout());
						list2_panel[i].setBackground(Color.white);
						img=(drink[i].state1).getImage();	img=img.getScaledInstance(80, 100, Image.SCALE_SMOOTH);
						drinkimg[i] = new JLabel(new ImageIcon(img));
						list2_name[i] = new JTextField(drink[i].name);
						list2_name[i].setPreferredSize(new Dimension(100, 40));	list2_name[i].setHorizontalAlignment(SwingConstants.CENTER);
						list2_price[i] = new JTextField(drink[i].price+"");		list2_price[i].setHorizontalAlignment(SwingConstants.CENTER);;
						list2_price[i].setPreferredSize(new Dimension(100, 40));
						list2_name[i].addActionListener(new ActionListener() {	// 이름 변경
							public void actionPerformed (ActionEvent e) {
								for(i=0;i<drink.length;i++) {
									if(e.getSource().equals(list2_name[i])){	// 해당 인덱스의 name 이벤트
										String text = list2_name[i].getText();
										try {
											drink[i].changeName(text);
											pricelabel[i].setText(drink[i].name+" "+drink[i].price+"원");
										}
										catch(InputMismatchException e1) {	// 잘못된 이름 입력
											new ExceptionDialog("잘못된 입력입니다.");
										}
									}
								}
							}
						});
						list2_price[i].addActionListener(new ActionListener() {	// 가격 변경
							public void actionPerformed (ActionEvent e) {
								for(i=0;i<drink.length;i++) {
									if(e.getSource().equals(list2_price[i])){	// 해당 인덱스의 price 이벤트
										try {
											int p = Integer.parseInt(list2_price[i].getText());
											drink[i].changePrice(p);
											pricelabel[i].setText(drink[i].name+" "+drink[i].price+"원");
											for(int j=0;j<drink.length;j++) {
												if(drink[j].amount>0) {
													if(pay.total>=drink[j].price)	drink[j].now=drink[j].state1;
													else							drink[j].now=drink[j].state2;
												}
												else 	drink[j].now=drink[j].state3;
												drinkButton[j].setIcon(drink[j].now);
											}
										}
										catch(NumberFormatException e1) {	// 잘못된 가격 입력
											new ExceptionDialog("잘못된 입력입니다.");
										}
									}
								}
							}
						});
						list2_panel[i].add(drinkimg[i]);	list2_panel[i].add(list2_name[i]);		list2_panel[i].add(list2_price[i]);
						M2_System.add(list2_panel[i]);
					}
					
					Menu[2].add(panel_back2, BorderLayout.NORTH);
					Menu[2].add(M2_System, BorderLayout.CENTER);
				cardBox.add(Menu[2], "메뉴설정");
			
			// Menu3 - 매출 산출
			Menu[3].setLayout(new BorderLayout());
			JPanel panel_back3 = new JPanel();	// 뒤로가기
				panel_back3.setBackground(Color.orange);
				BackIcon = new ImageIcon("./image/back(orange).png");
				img=BackIcon.getImage();	img=img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
				BackIcon.setImage(img);
				JButton Back3 = new JButton(BackIcon);		// 뒤로가기 버튼. Menu5 제외 모든 패널에 적용
				Back3.setBorderPainted(false); 	Back3.setFocusPainted(false);	Back3.setBackground(Color.orange);		
				Back3.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cd.show(cardBox, menu_name[5]);					
					}
				});
			panel_back3.add(Back3);
			
			JPanel M3_System = new JPanel();
				M3_System.setLayout(new BorderLayout());	// NORTH:ComboBox, CENTER: Table, SOUTH: stack
				// 콤보박스 패널
				JPanel panel3_cb = new JPanel();
					panel3_cb.setBackground(Color.white);
					String [] list_cb1 = {"매출", "재고소진"};			// ComboBox1::list
					String [] list_cb2 = {"일별", "월별"};			// ComboBox 종류에 따라 변경
					String [] list_cb3 = {"전체", drink[0].name, drink[1].name, drink[2].name, drink[3].name, drink[4].name};
					JComboBox<String> ComboBox1 = new JComboBox<String>(list_cb1);	// ComboBox1 : 매출/재고소진
					JComboBox<String> ComboBox2 = new JComboBox<String>(list_cb2);	// ComboBox2 : 일별/월별
					JComboBox<String> ComboBox3 = new JComboBox<String>(list_cb3);	// ComboBox3 : ComboBox2-메뉴
						ComboBox1.setFont(new Font("나눔스퀘어", Font.PLAIN, 15));
						ComboBox2.setFont(new Font("나눔스퀘어", Font.PLAIN, 15));
						ComboBox3.setFont(new Font("나눔스퀘어", Font.PLAIN, 15));
						ComboBox1.setPreferredSize(new Dimension(100, 30));
						ComboBox2.setPreferredSize(new Dimension(100, 30));
						ComboBox3.setPreferredSize(new Dimension(100, 30));
						ComboBox1.addActionListener(new ActionListener() {
							public void actionPerformed (ActionEvent e) {
								if(ComboBox1.getSelectedItem().equals("매출")) {
									ComboBox2.removeAllItems();
									ComboBox2.addItem(list_cb2[0]);
									ComboBox2.addItem(list_cb2[1]);
									ComboBox3.removeAllItems();
									for(int i=0;i<6;i++)
										ComboBox3.addItem(list_cb3[i]);
									ComboBox2.setEnabled(true);
									ComboBox3.setEnabled(true);
								}
								else {
									ComboBox2.removeAllItems();
									ComboBox2.setEnabled(false);	
									ComboBox3.removeAllItems();
									ComboBox3.setEnabled(false);
								}
							}
						});		
						
					// 조회 버튼	
					JButton search = new JButton("조회");		// 조회 버튼
						search.setFont(new Font("나눔스퀘어", Font.PLAIN, 15));
						search.setBackground(new Color(204,61,61));
						search.setForeground(Color.white);
					
				panel3_cb.add(ComboBox1);	panel3_cb.add(ComboBox2);	panel3_cb.add(ComboBox3);	panel3_cb.add(search);	
					
			// 본 내용 패널: JTable		// 날짜 | 총매출(원) | drink0(개) | drink1 | drink2 | drink3 | drink4 
				JPanel panel3_t = new JPanel();
					panel3_t.setBackground(Color.white);
					Vector <String> vector1 = new Vector<String>();
						vector1.addElement("날짜"); 	vector1.addElement("총매출");	// 열추가
						vector1.addElement(drink[0].name);	vector1.addElement(drink[1].name); 	vector1.addElement(drink[2].name);
						vector1.addElement(drink[3].name); 	vector1.addElement(drink[4].name);
					Vector <String> vector2 = new Vector<String>();
						vector2.addElement("날짜"); 	vector2.addElement("내용");
					Vector <String> vector3 = new Vector<String>();
						vector3.addElement("날짜"); 	vector3.addElement("매출");	vector3.addElement("판매수");
						
					DefaultTableModel dtm1 = new DefaultTableModel(vector1, 0);	// 매출 테이블 모델
					DefaultTableModel dtm2 = new DefaultTableModel(vector2, 0);	// 재고소진 테이블 모델
					DefaultTableModel dtm3 = new DefaultTableModel(vector3, 0);	// 매출-메뉴 테이블 모델
					JTable table = new JTable();	// 테이블 생성
						search.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {	// 테이블 데이터 저장
								String cb1 = ComboBox1.getSelectedItem().toString();
								FileReader file;
								Vector<String> data;
								String fileName = null;		

								if(cb1.equals("매출")) {		// ComboBox1 => "매출"
									String cb2 = ComboBox2.getSelectedItem().toString();
									String cb3 = ComboBox3.getSelectedItem().toString();
									dtm1.setNumRows(0);		// dtm1 초기화
									dtm3.setNumRows(0);
									// 파일 지정
									if(cb2.equals("일별")) {
										if(cb3.equals("전체"))	fileName = new String("./매출_일별.txt");
										else if(cb3.equals(drink[0].name))	fileName = new String("./매출_일별0.txt");
										else if(cb3.equals(drink[1].name))	fileName = new String("./매출_일별1.txt");
										else if(cb3.equals(drink[2].name))	fileName = new String("./매출_일별2.txt");
										else if(cb3.equals(drink[3].name))	fileName = new String("./매출_일별3.txt");
										else								fileName = new String("./매출_일별4.txt");
									}
									else {
										if(cb3.equals("전체"))	fileName = new String("./매출_월별.txt");
										else if(cb3.equals(drink[0].name)) 	fileName = new String("./매출_월별0.txt");
										else if(cb3.equals(drink[1].name))	fileName = new String("./매출_월별1.txt");
										else if(cb3.equals(drink[2].name)) 	fileName = new String("./매출_월별2.txt");
										else if(cb3.equals(drink[3].name))	fileName = new String("./매출_월별3.txt");
										else 								fileName = new String("./매출_월별4.txt");									
									}
									
									try {
										file = new FileReader(fileName);
										Scanner scanner = new Scanner(file);
										
										if(cb3.equals("전체")) {
											while(scanner.hasNextLine()) {
												data = new Vector<String>();
												for(i=0;i<7;i++)
													data.add(scanner.next());
												dtm1.addRow(data);
											}
										}
										else {		// 각 메뉴에 대한 매출
											while(scanner.hasNextLine()) {
												data = new Vector<String>();
												for(i=0;i<3;i++)
													data.add(scanner.next());
												dtm3.addRow(data);
											}
										}
										scanner.close();
										file.close();
									}
									
									catch(IOException e1) {		// 해당 파일이 없음(매출파일)
										new ExceptionDialog("file not found! :: "+fileName);
									}
									
									if(cb3.equals("전체"))	table.setModel(dtm1); 	// 구성된 테이블모델로 테이블 설정	
									else					table.setModel(dtm3);
								}
								else {		// ComboBox1 => "재고소진"
									dtm2.setNumRows(0);
									try{
										fileName = new String("./재고소진.txt");
										file = new FileReader(fileName);
										Scanner scanner = new Scanner(file);
										while(scanner.hasNextLine()) {
											data = new Vector<String>();
												data.add(scanner.next());	// 날짜 저장 
												data.add(scanner.next());	// 매진된 메뉴 내용 저장
											dtm2.addRow(data);
										}
										scanner.close();
										file.close();
									}
									catch(IOException e1) {		// 재고소진 파일이 없음
										new ExceptionDialog("file not found! :: 재고소진.txt");
										dtm2.setNumRows(0); 	// dtm2 초기화
									}
								table.setModel(dtm2);
								}
							}
						});
						
					JScrollPane scroll = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
							ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);	// 스크롤 설정(수직 스크롤만 사용)
							
					panel3_t.add(scroll);	
						
				M3_System.add(panel3_cb, BorderLayout.NORTH);
				M3_System.add(panel3_t, BorderLayout.CENTER);
						
						
				Menu[3].add(panel_back3, BorderLayout.NORTH);
				Menu[3].add(M3_System, BorderLayout.CENTER);
			cardBox.add(Menu[3], "매출산출");	
			
			
			
			// Menu4 - 비밀번호 설정
			Menu[4].setLayout(new BorderLayout());
				JPanel panel_back4 = new JPanel();	// 뒤로가기
					panel_back4.setBackground(Color.orange);
					BackIcon = new ImageIcon("./image/back(orange).png");
					img=BackIcon.getImage();	img=img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
					BackIcon.setImage(img);
				JButton Back4 = new JButton(BackIcon);		// 뒤로가기 버튼. Menu5 제외 모든 패널에 적용
					Back4.setBorderPainted(false); 	Back4.setFocusPainted(false);	Back4.setBackground(Color.orange);		
					Back4.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							cd.show(cardBox, menu_name[5]);					
						}
					});
					panel_back4.add(Back4);
				
				JPanel M4_System = new JPanel();
					M4_System.setBackground(Color.white);
					M4_System.setLayout(new GridLayout(5,1));
					JPanel m4_panel1 = new JPanel();	// 변경할 비밀번호
					JPanel m4_panel2 = new JPanel();	// 비밀번호 재입력
					JPanel m4_panel3 = new JPanel();
						JLabel m4_label1 = new JLabel("비밀번호 변경");
						JLabel m4_label2 = new JLabel("비밀번호 재입력");
						JPasswordField m4_pf1 = new JPasswordField();
						JPasswordField m4_pf2 = new JPasswordField();
							m4_pf1.setPreferredSize(new Dimension(100, 20));
							m4_pf2.setPreferredSize(new Dimension(100, 20));
							m4_label1.setLabelFor(m4_pf1);
							m4_label1.setFont(new Font("나눔스퀘어", Font.PLAIN, 20));
							m4_label2.setLabelFor(m4_pf2);
							m4_label2.setFont(new Font("나눔스퀘어", Font.PLAIN, 20));
						JButton m4_b = new JButton("변경");
							m4_b.setBackground(Color.orange);				
							m4_panel1.add(m4_label1);	m4_panel1.add(m4_pf1);
							m4_panel2.add(m4_label2);	m4_panel2.add(m4_pf2);
							m4_panel3.add(m4_b);
							m4_b.setFont(new Font("나눔스퀘어", Font.PLAIN, 20));
							m4_b.addActionListener(new ActionListener() {	// 비밀번호 변경 이벤트
								public void actionPerformed(ActionEvent e) {
									boolean check_pf, check_isPW;
									// check_pf: 두 필드가 같은가? check_isPW: 비밀번호 조건을 충족하는가?
									check_pf = m4_pf1.getText().equals(m4_pf2.getText());		// 필드 비교
									if(check_pf==false) {	// 두 필드가 다름
										JDialog failMenu4 = new JDialog();		// 비밀번호 변경 실패 안내 다이얼로그
											failMenu4.setSize(250, 200);
											failMenu4.setLayout(new BorderLayout());
											JLabel failMenu4_l = new JLabel("잘못된 입력입니다.\n다시 시도하세요.");
												failMenu4_l.setFont(new Font("나눔스퀘어", Font.PLAIN, 15));
												failMenu4_l.setHorizontalAlignment(SwingConstants.CENTER);
												failMenu4_l.setVerticalAlignment(SwingConstants.CENTER);
												JButton failMenu4_b = new JButton("OK");
												failMenu4_b.setBackground(Color.orange);
												failMenu4_b.addActionListener(new ActionListener() {
													public void actionPerformed(ActionEvent e1) {
														failMenu4.setVisible(false);
													}
												});
												failMenu4.add(failMenu4_l, BorderLayout.CENTER);
												failMenu4.add(failMenu4_b, BorderLayout.SOUTH);
											failMenu4.setVisible(true);
											failMenu4.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
									}
																		
									else if(check_pf==true) {	
										check_isPW = password.isPW(m4_pf2.getText());	// 비밀번호 조건 충족?
										
										// 두 필드가 같으나 비밀번호 조건 불총족
										if(check_isPW==false) {
											new ExceptionDialog("8자리 이상의 숫자와 특수문자를 입력하세요");
										}
										else {	// 두 필드가 같고 비밀번호 조건 충족 => 변경 가능
											password.changePW(m4_pf2.getText());
											JDialog successMenu4 = new JDialog();	// 변경 성공 안내 다이얼로그
												successMenu4.setSize(250, 200);
												successMenu4.setLayout(new BorderLayout());
												JLabel successMenu4_l = new JLabel("비밀번호가 변경되었습니다.");
												JButton successMenu4_b = new JButton("OK");
													successMenu4_l.setFont(new Font("나눔스퀘어", Font.PLAIN, 15));
													successMenu4_l.setHorizontalAlignment(SwingConstants.CENTER);
													successMenu4_l.setVerticalAlignment(SwingConstants.CENTER);
													successMenu4_b.setBackground(Color.orange);
													successMenu4_b.addActionListener(new ActionListener() {
														public void actionPerformed (ActionEvent e1) {
															successMenu4.setVisible(false);	// 안내창 사라짐
															cd.show(cardBox, menu_name[5]);	// 메뉴창으로 자동 이동
														}
													});
												successMenu4.add(successMenu4_l, BorderLayout.CENTER);
												successMenu4.add(successMenu4_b, BorderLayout.SOUTH);
											successMenu4.setVisible(true);
											successMenu4.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
										}
									}
								}
							});
						M4_System.add(new JPanel());
						M4_System.add(m4_panel1);
						M4_System.add(m4_panel2);
						M4_System.add(m4_panel3);
						M4_System.add(new JPanel());
					Menu[4].add(panel_back4, BorderLayout.NORTH);
					Menu[4].add(M4_System, BorderLayout.CENTER);
				cardBox.add(Menu[4], "비밀번호");

			c.add(cardBox);
		}
	}

// JTable 내용 파일에서 읽어오기
	class RaedTabelData{
		void ReadTableDate() {
			try{
				FileReader fr = new FileReader("./매출.txt");
				String data[]= new String[7]; // 날짜 | 총매출 | 0 | 1 | 2 | 3 | 4
					int a=0;

					Scanner scanner = new Scanner(fr);
					while((a=fr.read())!=-1) {		// fr 끝까지 반복
						for(int i=0;i<7;i++) {
							data[i]=scanner.next();		// data[]에 나누어 저장
						}
						
					}
					
				scanner.close();
				fr.close();
			}
			catch(IOException e){		// 매출 파일이 없음
				new ExceptionDialog("file not found! :: 매출.txt");
			}
		}
	}
	
	public static void main(String[] args) {
		new VendingMachine();
	}
}
