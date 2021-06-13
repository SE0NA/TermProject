// 타자 게임

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <conio.h>
#include <time.h>
#include <windows.h>
#include <process.h>

#define UP 72
#define DOWN 80
#define LEFT 75
#define RIGHT 77
#define ENTER 13
#define ESC 27
#define SPACE 32
#define ESC 27

static int LIFE = 3;
static int ALL = 0;

extern void title(void);
extern user_number;
extern void gotoxy(int, int);

void logo(void);
void manual(void);
void game(void);
void box (void);
void life(void);

void game_main(void)
{
restart:
	logo();
	manual();
	int key;
	key = _getch();
	system("cls");
	game();
	system("cls");
	logo();
	printf("\n\n\n");
	printf("\t\t         당신이 총 입력한 단어수: %d", ALL);
	printf("\n\t\t     ※ESC를 누르면 메인으로 돌아갑니다.※");
	printf("\n\t\t      ※아무키나 누르면 다시 시작합니다.※\n");
	key = _getch();
	system("cls");
	if (key == ESC)
		return;
	else
		goto restart;
}

void logo(void)
{
	printf("\n\n\n\t");
	printf("    ■■■■■■■■■■■■■■■■■■■■■■■■■■■\n\t");
	printf("  ■   ▣▣▣        ▣         ▣      ▣      ▣▣▣▣  ■\n\t");
	printf("  ■  ▣    ▣      ▣▣      ▣  ▣  ▣  ▣    ▣        ■\n\t");
	printf("  ■ ▣            ▣  ▣     ▣  ▣  ▣  ▣    ▣        ■\n\t");
	printf("  ■ ▣           ▣    ▣   ▣    ▣▣    ▣   ▣▣▣▣  ■\n\t");
	printf("  ■ ▣    ▣▣   ▣▣▣▣   ▣     ▣     ▣   ▣        ■\n\t");
	printf("  ■  ▣    ▣    ▣    ▣   ▣     ▣     ▣   ▣        ■\n\t");
	printf("  ■    ▣▣      ▣    ▣   ▣            ▣   ▣▣▣▣  ■\n\t");
	printf("    ■■■■■■■■■■■■■■■■■■■■■■■■■■■\n");
}
void manual(void) {
	printf("\n\n\n");
	printf("\t\t     화면에 나타나는 랜덤의 단어들 입력하여\n");
	printf("\t\t     사라지게 하는 게임입니다.\n\n");
	printf("\t\t     LIFE는 총 3개로 틀렸을 때 사라집니다.\n");
	printf("\n\t\t                      시작하려면 아무키나 누르시오...");
}

void game(void) {
	char word[70][30] = { "복숭아", "딸기", "레몬", "오렌지", "귤", "수박","사과","파인애플","살구", "키위",
							"산","바다","하늘","달","별","태양","지구","심해","오로라","빙하",
							"안개","비","구름","천둥","소나기","눈","우박","화창","바람","황사",
							"고구마","감자","상추","배추","버섯","파프리카","양파","파","당근","토마토",
							"나무","오두막","장작","불빛","탁자","자연","벽난로","절벽","덩쿨","꽃",
							"창문","녹차","과자","조명","시계","시험","출입문","모종삽","리모컨","사진",
							"작품","컴퓨터","키보드","프로그램","안경","뚜껑","손수건","연필","숟가락","젓가락"};
						
							

	char input[30] = { NULL };
	char text[5][30];
	srand(time(NULL));
	LIFE = 3;
	while (1) {
		system("cls");
		int x[5] = { 0 }; int y[5] = { 0 };
		int enter = 0;
		int i = 0;
		int k = 0;
		for (int k = 0; k < 5; k++) {
		reloc:
			i += rand() % 11;
			x[k] = rand() % 70 + 3;
			y[k] = rand() % 22 + 2;
			if (k != 0)
				for (int j = 0; j < k; j++)
					if (x[j] / 7 == x[k] / 7)
						goto reloc;

			gotoxy(x[k], y[k]);
			printf("%s", word[i]);
			strcpy(text[k], word[i]);
			i++;
			gotoxy(0, 0);
		}
		while (enter < 5) {
			box();
			life();
			gotoxy(11, 26); gets_s(input, 20);
			gotoxy(11, 26);
			printf("            ");
			for (k = 0; k < 5; k++) {
				if (strcmp(input, text[k]) == 0) {
					gotoxy(x[k], y[k]);
					printf("       ");
					enter++;
					ALL++;
					break;
				}
			}
			if (strcmp(input,text[k])!=0) {
				LIFE--;
				gotoxy(65, 28);	printf("         ");
				life();
			}
			if (LIFE == 0)
				return 0;
		}
		system("cls");

	}

	
}
void box(void)
{
	gotoxy(1, 25);	printf("+----------------------------------------------------------------------+");
	gotoxy(1, 26);	printf("| ENTER |                                                              |");
	gotoxy(1, 27);	printf("+----------------------------------------------------------------------+");
}
void life(void)
{
	gotoxy(67, 28);
	for (int i = 0; i < LIFE; i++)
		printf("♥");
}
