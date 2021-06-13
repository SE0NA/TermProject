// 타자 연습

#include <stdio.h>
#include <stdlib.h>
#include <windows.h>
#include <string.h>
#include <conio.h>
#include <ctype.h>
#include <time.h>

#define UP 72
#define DOWN 80
#define LEFT 75
#define RIGHT 77
#define ENTER 13
#define ESC 27
#define SPACE 32

static int point ;

extern void title(void);
extern int user_number;
extern void gotoxy(int, int);

void cursor_position(void);
void cursor_word(void);
void cursor_short(void);
void cursor_eng(void);
void cursor_kor(void);

int account(void);

void p_eng(int);
void p_kor(int);
void p_design(void);

void w_kor(int);
void w_eng(int);
void w_design(void);

void blurp(int);
void b_design(void);

void practice_main(void)
{
	int key;
	int menu = 0;
	cursor_position();
	while (1)	//연습 메뉴 선택
	{
		key = _getch();

		if (menu > 0 && key == UP) {
			if (menu == 1) {
				menu--;
				cursor_position();
			}
			else if (menu == 2) {
				menu--;
				cursor_word();
			}
		}
		else if (menu < 2 && key == DOWN) {
			if (menu == 0) {
				menu++;
				cursor_word();
			}
			else if (menu == 1) {
				menu++;
				cursor_short();
			}
		}
		else if (key == ENTER)
			break;
	}

	int language = 0;		//language =0: 한글 / =1: ENG
	if (menu != 2) {
		cursor_kor();
		while (1) {
			key = _getch();
			if (language == 1 && key == UP) {
				cursor_kor();
				language--;
			}
			else if (language == 0 && key == DOWN) {
				cursor_eng();
				language++;
			}
			else if (key == ENTER)
				break;
		}
	}

	int count;

	if (menu == 0 && language == 0) {	//자리연습 한글
		while (1) {
			count = account();
			p_kor(count);
			system("cls");	 title();
			printf("\n\n\t\t    점수: %d / 100점", point);
			printf("\n\n\t\t다시 시작하려면 아무키나 누르시오...");
			printf("\n\t\t\t   ESC는 메뉴로\n");
			key = _getch();
			if (key == ESC) {
				system("cls");	return;
			}
				
		}
	}

	else if (menu == 0 && language == 1) {	//자리연습 영어
		while (1) {
			count = account();
			p_eng(count);
			system("cls");	 title();
			printf("\n\n\t\t    점수: %d / 100점", point);
			printf("\n\t\t다시 시작하려면 아무키나 누르시오...");
			printf("\n\t\t\t   ESC는 메뉴로\n");
			key = _getch();
			if (key == ESC) {
				system("cls");	return;
			}
		}
	}

	else if (menu == 1 && language == 0) {
		while (1) {
			count = account();
			w_kor(count);
			system("cls");	 title();
			printf("\n\n\t\t    점수: %d / 100점", point);
			printf("\n\n\t\t다시 시작하려면 아무키나 누르시오...");
			printf("\n\t\t\t   ESC는 메뉴로\n");
			key = _getch();
			if (key == ESC) {
				system("cls");	return;
			}
		}
	}
	else if (menu == 1 && language == 1) {
		while (1) {
			count = account();
			w_eng(count);
			system("cls");	 title();
			printf("\n\n\t\t    점수: %d / 100점", point);
			printf("\n\n\t\t다시 시작하려면 아무키나 누르시오...");
			printf("\n\t\t\t   ESC는 메뉴로\n");
			key = _getch();
			if (key == ESC) {
				system("cls");	return;
			}
		}
	}
	else if (menu == 2) {
		while (1) {
			count = account();
			blurp(count);
			printf("\n\n\t\t다시 시작하려면 아무키나 누르시오...");
			printf("\n\t\t\t   ESC는 메뉴로\n");
			key = _getch();
			if (key == ESC) {
				system("cls");	return;
			}
		}
	}
}

//메뉴선택 커서 이동 함수
void cursor_position(void)
{
	system("cls");	title();
	printf("\n\n");
	printf("\t                          ◆자리 연습◆\n");
	printf("\t                          ◇낱말 연습◇\n");
	printf("\t                        ◇짧은 글 연습◇\n\n\n\n");
}
void cursor_word(void)
{
	system("cls");	title();
	printf("\n\n");
	printf("\t                          ◇자리 연습◇\n");
	printf("\t                          ◆낱말 연습◆\n");
	printf("\t                        ◇짧은 글 연습◇\n\n\n\n");
}
void cursor_short(void)
{
	system("cls");	title();
	printf("\n\n");
	printf("\t                          ◇자리 연습◇\n");
	printf("\t                          ◇낱말 연습◇\n");
	printf("\t                        ◆짧은 글 연습◆\n\n\n\n");
}

//언어 선택
void cursor_kor(void)
{
	system("cls");	title();
	printf("\n\n");
	printf("\t                            ◆한 글◆\n");
	printf("\t                           ◇ENGLISH◇\n\n\n\n");
}
void cursor_eng(void)
{
	system("cls");	title();
	printf("\n\n");
	printf("\t                            ◇한 글◇\n");
	printf("\t                           ◆ENGLISH◆\n\n\n\n");

}

int account(void) {
	int result;
	system("cls");	title();
	printf("\n\t입력 횟수를 입력하시오: ");
	scanf("%d", &result);
	getchar();
	return result;
}

//자리연습 함수
void p_eng(int count)
{
	int correct = 0;
	int i;	
	char input;

	srand((unsigned)time(NULL));

	for(int j=0;j<count;j++) {
		system("cls");
		gotoxy(11, 2);	printf("%d / %d\n", correct, count);
		p_design();
		i = (rand() % 26);
		gotoxy(38, 7);
		printf("%c", 97 + i);
		gotoxy(38, 14);
		input = getchar();
		getchar();
		if (97 + i == (int)input)
			correct++;
	}
	point = ((double)correct / count)*100;
}
void p_kor(int count)
{
	int correct = 0;
	unsigned char letter[26][3] = { "ㄱ","ㄴ","ㄷ","ㄹ","ㅁ","ㅂ","ㅅ","ㅇ","ㅈ","ㅊ","ㅋ","ㅍ","ㅌ","ㅎ","ㅏ","ㅑ","ㅓ","ㅕ","ㅗ","ㅛ","ㅜ","ㅠ","ㅡ","ㅣ","ㅐ","ㅔ" };
	int i;	int q = 0;
	srand((unsigned)time(NULL));
	while (q < count) {
		system("cls");	
		gotoxy(11, 2);	printf("%d / %d\n", correct, count);
		p_design();
		i = rand() % 26;
		char input[3] = { NULL };
		gotoxy(38, 7);
		printf("%s", letter[i]);
		gotoxy(38, 14);
		gets_s(input, 3);

		if (strcmp(letter[i],input)==0)
			correct++;
		q++;
	}
	point = ((double)correct / count)*100;
}
void p_design(void) {
	gotoxy(7, 3);	printf("  ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
	gotoxy(7, 4);	printf("■■                                                        ■■");
	gotoxy(7, 5);	printf("■                                                            ■");
	gotoxy(7, 6);	printf("■                                                            ■");
	gotoxy(7, 7);	printf("■                                                            ■");
	gotoxy(7, 8);	printf("■                                                            ■");
	gotoxy(7, 9);	printf("■                                                            ■");
	gotoxy(7, 10);	printf("■                                                            ■");
	gotoxy(7, 11);	printf("■                                                            ■");
	gotoxy(7, 12);	printf("■                                                            ■");
	gotoxy(7, 13);	printf("■                                                            ■");
	gotoxy(7, 14);	printf("■                                                            ■");
	gotoxy(7, 15);	printf("■                                                            ■");
	gotoxy(7, 16);	printf("■                                                            ■");
	gotoxy(7, 17);	printf("■                                                            ■");
	gotoxy(7, 18);	printf("■■                                                        ■■");
	gotoxy(7, 19);	printf("  ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");

	gotoxy(34, 6);	printf("+--------+");
	gotoxy(34, 7);	printf("|        |");
	gotoxy(34, 8);	printf("+--------+");
	gotoxy(24, 13);	printf("+--------------------------+");
	gotoxy(24, 14);	printf("| ENTER |                  |");
	gotoxy(24, 15);	printf("+--------------------------+");
}


//낱말연습 함수
void w_kor(int count)
{
	int correct = 0;
	int i;	int q = 0;
	char word[30][20] = { "각골난망", "낙화유수", "도원결의", "명약관화", "백년지객", "사필귀정", "오매불망", "적반하장", "청풍명월", "쾌독파차",
						"태평무상", "풍운지회", "허심탄회", "가화만사성", "공생명", "가기만고당", "마철저", "유학부지족", "계륵", "파천황",
						"미증유", "태두", "대기만성", "형설지공", "어부지리","삼고초려", "절식개운", "유비무환", "개과천선", "책인서기" };

	srand((unsigned)time(NULL));
	while (q < count) {
		system("cls");
		gotoxy(14, 2);	printf("%d / %d\n", correct, count);
		w_design();
		i = rand() % 29;
		char input[20] = { NULL };
		gotoxy(35, 7);
		printf("%s", word[i]);
		gotoxy(36, 14);
		gets_s(input, 20);

		if (strcmp(word[i], input) == 0)
			correct++;
		q++;
	}
	point = ((double)correct /count) *100;
}
void w_eng(int count)
{
	int correct = 0;	int i;	int q = 0;
	char word[30][15] = { "apple", "galaxy", "program", "print", "string", "compare","paste", "library","output", "input",
							"buffer", "alpha", "function", "process", "include", "utopia", "multimedia", "minute", "hangover", "movie",
							"chicken", "pizza","white","black","specify","troble","settle","shoot","grocery","run" };
	srand((unsigned)time(NULL));

	while (q < count) {
		system("cls");
		gotoxy(14, 2); printf("%d / %d\n", correct, count);
		w_design();
		i = rand() % 21;
		char input[15] = { NULL };
		gotoxy(35, 7);
		printf("%s", word[i]);
		gotoxy(36, 14);
		gets_s(input, 15);

		if (strcmp(word[i], input) == 0)
			correct++;
		q++;
	}
	point = ((double)correct / count)*100;
}
void w_design(void)
{
	gotoxy(7, 3);	printf("  ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
	gotoxy(7, 4);	printf("■■                                                        ■■");
	gotoxy(7, 5);	printf("■                                                            ■");
	gotoxy(7, 6);	printf("■                                                            ■");
	gotoxy(7, 7);	printf("■                                                            ■");
	gotoxy(7, 8);	printf("■                                                            ■");
	gotoxy(7, 9);	printf("■                                                            ■");
	gotoxy(7, 10);	printf("■                                                            ■");
	gotoxy(7, 11);	printf("■                                                            ■");
	gotoxy(7, 12);	printf("■                                                            ■");
	gotoxy(7, 13);	printf("■                                                            ■");
	gotoxy(7, 14);	printf("■                                                            ■");
	gotoxy(7, 15);	printf("■                                                            ■");
	gotoxy(7, 16);	printf("■                                                            ■");
	gotoxy(7, 17);	printf("■                                                            ■");
	gotoxy(7, 18);	printf("■■                                                        ■■");
	gotoxy(7, 19);	printf("  ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");

	gotoxy(26, 6);	printf("+-----------------------+");
	gotoxy(26, 7);	printf("|                       |");
	gotoxy(26, 8);	printf("+-----------------------+");

	gotoxy(25, 13);	printf("+--------------------------+");
	gotoxy(25, 14);	printf("| ENTER |                  |");
	gotoxy(25, 15);	printf("+--------------------------+");
}

//짧은 글 함수
void blurp(int count)
{
	double typing = 0;
	int i;	int correct = 0;	int q = 0;
	char line[15][100] = { "간에 붙었다 쓸개에 붙었다 한다.", "강 건너 불구경하듯 하다.", "남의 떡이 더 커 보인다.",
							"낮말은 새가 듣고 밤말은 쥐가 듣는다.", "서당 개 삼 년이면 풍월을 읊는다.", "설마가 사람 잡는다.",
							"소 잃고 외양간 고친다.","쥐구멍에도 볕 들 날 있다.","지렁이도 밟으면 꿈틀한다.",
							"콩 심은 데 콩 나고 팥 심은 데 팥 난다.", "하늘이 무너져도 솟아날 구멍이 있다.", "물에 빠진 놈 건져 놓으니까 봇짐 내라 한다.",
							"믿는 도끼에 발등 찍힌다.", "바늘 가는데 실 간다.", "윗물이 맑아야 아랫물이 맑다." };

	srand((unsigned)time(NULL));
	while (q < count) {
		system("cls");
		gotoxy(4, 3);	printf("%d / %d\n", correct, count);
		i = rand() % 14;
		char input[100] = { NULL };
		b_design();
		gotoxy(10, 6);
		printf("%s", line[i]);
		gotoxy(12, 15);
		clock_t start, end;
		start = clock();
		gets_s(input, 100);
		end = clock();
		
		double time;
		time = (double)(end - start) / 1000;
		if (typing == 0)
			typing = (strlen(input) / 2) / time;
		else
			typing = (typing + (strlen(input) / 2 / time)) / 2;
		if (strcmp(line[i], input) == 0)
			correct++;
		q++;
	}
	
	point = ((double)correct / count)*100;
	system("cls"); title();
	printf("\n\n\t\t타수: %d", (int)(typing * 100));
	printf("\n\t\t점수: %d / 100 점\n", point);
}
void b_design(void) 
{
	gotoxy(3, 4);	printf("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
	gotoxy(3, 5);	printf("■                                                                      ■");
	gotoxy(3, 6);	printf("■                                                                      ■");
	gotoxy(3, 7);	printf("■                                                                      ■");
	gotoxy(3, 8);	printf("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");

	gotoxy(2, 14);	printf("+---------------------------------------------------------------------------+");
	gotoxy(2, 15);	printf("| ENTER |                                                                   |");
	gotoxy(2, 16);	printf("+---------------------------------------------------------------------------+");
}
