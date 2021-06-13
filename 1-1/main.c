/*
    2019-2 소프트웨어기초설계
    타자 연습 프로그램
*/

#include <stdio.h>
#include <stdlib.h>
#include <windows.h>
#include <string.h>
#include <conio.h>
#include <ctype.h>

#define UP 72
#define DOWN 80
#define LEFT 75
#define RIGHT 77
#define ENTER 13
#define ESC 27
#define SPACE 32

extern void practice_main(void);

void title(void);
void cursor_hide(int);
void new_user_cursor(void);
void already_user_cursor(void);
void gotoxy(int x, int y);
void new_user_input(float *name);
int user_list(void);
void user_list_print(void);
int already_user_input(float *name);
void practice_cursor(void);
void game_cursor(void);

int user_number;		//현재 사용자의 텍스트파일내의 번호(라인)

int main(void) //메뉴 선택
{
	system("mode con cols=81 lines=30");


	title();
	new_user_cursor();
	char name[11] = { 0 };
	int key = 0;
	//1차 메뉴 -- 완료
	while(1)
	{
		static int menu_1 = 0;
		key = _getch();

		if (menu_1 == 1 && key == UP){
			system("cls");
			title();	new_user_cursor();
			menu_1 = 0;
		}
		else if (menu_1 == 0 && key == DOWN){
			system("cls");
			title();	already_user_cursor();
			menu_1 = 1;
		}
		else if (key == ENTER)	//사용자 선택 (추가&불러오기)
		{
			if (menu_1 == 0){
			no_user_list:
				system("cls");
				title();
				new_user_input(&name);
				system("cls");
				break;
			}
			else{
				system("cls");

				if(already_user_input(&name)==-1)
					goto no_user_list;
				system("cls");
				break;
			}
		}
	}

	//2차 메뉴
remenu:
	title();
	printf("\t환영합니다, %s님!\n", name);
	practice_cursor();

	int menu_2 = 0;
	while (1)					//메뉴 선택하기
	{
		key = _getch();

		if (menu_2 != 0 && key == UP){
			system("cls");	title();		printf("\t환영합니다, %s님!\n", name);
			practice_cursor();
			menu_2--;
		}
		else if (menu_2 != 1 && key == DOWN){
			system("cls");	title();		printf("\t환영합니다, %s님!\n", name);
			game_cursor();
			menu_2++;
		}
		else if (key == ENTER)
			break;
	}
	if (menu_2 == 0)		//2차 메뉴 - 연습 선택완료
	{
		system("cls");
		practice_main();
		system("cls");
		goto remenu;
	}
	else if (menu_2 == 1)	//2차 메뉴 - 게임 선택완료
	{
		system("cls");
		game_main();
		system("cls");
		goto remenu;
	}


	return 0;
}
    

void title(void)
{
	printf("\n\n\n\t");
	printf("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■\n\t");
	printf("■                                                          ■\n\t");
	printf("■                         □□                             ■\n\t");
	printf("■                     □□□   □                          ■\n\t");
	printf("■             +----------------------------+               ■\n\t");
	printf("■        □□ |  TYPING PRACTICE           |               ■\n\t");
	printf("■     □□□  |            FOR BEGINER     |               ■\n\t");
	printf("■  □□□□   +----------------------------+               ■\n\t");
	printf("■□□□□             □                □□             □■\n\t");
	printf("■□□□                                    □□        □□■\n\t");
	printf("■□□                                          □□  □□□■\n\t");
	printf("■□□                                             □□     ■\n\t");
	printf("■□                                                   □   ■\n\t");
	printf("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■\n\t");
	printf("\n\n\n");
}
void cursor_hide(int i)
{	//숨기려면 i=0, 보이려면 i=1
	CONSOLE_CURSOR_INFO c = { 0 };
	c.bVisible = i;
	SetConsoleCursorInfo(GetStdHandle(STD_OUTPUT_HANDLE), &c);
}

//1차 메뉴
void new_user_cursor(void)
{
	printf("\t                      ◆사용자 추가◆\n");
	printf("\t                      ◇기존 사용자◇\n\n\n\n");
}	  
void already_user_cursor(void)
{
	printf("\t                      ◇사용자 추가◇\n");
	printf("\t                      ◆기존 사용자◆\n\n\n\n");
}
void gotoxy(int x, int y)
{
	COORD coord = { x,y };
	coord.X = x;	coord.Y = y;
	SetConsoleCursorPosition(GetStdHandle(STD_OUTPUT_HANDLE), coord);
}
void new_user_input(float *name)
{
name_remove:
	printf("\t                      Name: __________\n\n");
	printf("\t                ※영문만으로 10자 입력 가능※\n");
	printf("\t                    ※ENTER 입력 시 확인※\n");

	gotoxy(36, 20);
	char tmp_name[10];
	gets_s(name, 10);
	int i = 0;


	FILE*fp = fopen("userlist.txt", "a+");
	int line_num = 0;
	char buffer[20];
	while (fgets(buffer, 20, fp))			//텍스트 파일에 이미 존재하는 이름인지 확인
	{
		line_num++;
		if (strstr(buffer, (char*)name)) {
			system("cls");	title();
			printf("                        이미 존재하는 사용자입니다.\n");
			Sleep(1500);
			system("cls");	title();
			goto name_remove;
		}
	}

	int new_user_menu = 0;
	system("cls");	title();
	printf("\t\t         \"%s\"로 하시겠습니까?\n", (char*)name);
	printf("\t\t        ◆YES◆            ◇NO◇\n");

	int key2 = 0;
	while (key2 != ENTER)
	{
		key2 = _getch();
		if (new_user_menu == 0 && key2 == RIGHT)
		{
			new_user_menu = 1;
			system("cls");	title();
			printf("\t\t         \"%s\"로 하시겠습니까?\n", (char*)name);
			printf("\t\t        ◇YES◇            ◆NO◆\n");
		}
		else if (new_user_menu == 1 && key2 == LEFT)
		{
			new_user_menu = 0;
			system("cls");	title();
			printf("\t\t         \"%s\"로 하시겠습니까?\n", (char*)name);
			printf("\t\t        ◆YES◆            ◇NO◇\n");
		}
	}
	if (new_user_menu == 1) {
		system("cls");	title();
		goto name_remove;
	}

	line_num = 0;
	while (fgets(buffer, 20, fp) != NULL)
	{
		line_num++;
		if (strstr(buffer, (char*)name)) {
			user_number = line_num;
		}
	}

	fputs(name, fp);
	fputs("\n", fp);
	fclose(fp);	
}
int user_list(void)
{
	FILE *fp = fopen("userlist.txt", "r");

	if (fp == NULL)
	{
		printf("----User List-------\n");
		printf("\n\n   저장된 사용자가 없습니다.");
		Sleep(2000);
		return 0;
	}

	user_list_print();
	return 1;
}
void user_list_print(void)
{
	FILE *fp = fopen("userlist.txt", "r");
	printf("----User List-------\n");
	int i = 0;
	while (!feof(fp))
	{
		char tmp[11] = { NULL };
		fscanf(fp, "%s", tmp);
		printf("    %s\n", tmp);
		i++;
	}
	fclose(fp);
}
int already_user_input(float *name)
{
	int choose = 0;
	int key;
	
	if (user_list() == 0)	//유저리스트가 존재하지 않아 사용자 추가로 자동으로 보냄
		return -1;

	gotoxy(0, 1);
	printf("◆");

	FILE *fp = fopen("userlist.txt", "r");
	int  final_user= 0;

	while (!feof(fp))
	{
		char tmp[11] = { NULL };
		fgets(tmp, sizeof(tmp), fp);
		final_user++;
	}	final_user = final_user - 2;					//사용자 수 구하기

	while (1)
	{
		key = _getch();
		if (choose < final_user && key == DOWN) {
			system("cls");	user_list_print();
			choose++;
			gotoxy(0, choose+1);
			printf("◆");
		}
		else if (choose > 0 && key == UP) {
			system("cls");	user_list_print();
			choose--;
			gotoxy(0, choose+1);
			printf("◆");
		}
		else if (key == ENTER)
			break;
	}
	user_number = choose;

	rewind(fp);
	for (int i = 0; !feof(fp); i++) {
		char tmp[11];
		fgets((char*)name, strlen(tmp), fp);
		if (i == choose) {
			break;
		}
	}
	fclose(fp);
	strtok((char*)name, "\n");
	return 0;
}

//2차 메뉴 커서
void practice_cursor(void) 
{
	printf("\n\n");
	printf("\t                          ◆연습◆\n");
	printf("\t                          ◇게임◇\n\n\n\n");
}
void game_cursor(void)
{
	printf("\n\n");
	printf("\t                          ◇연습◇\n");
	printf("\t                          ◆게임◆\n\n\n\n");
}
