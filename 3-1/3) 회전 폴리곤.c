#include <GL/glut.h>
#include <stdlib.h>
#include <string.h>

typedef struct node{
	int x, y;				// 좌표 값
	int num;				// 정점 번호
	struct node *link;		// 다음 노드
} node;

typedef struct list{
	node *head, *tail;
} list;
list nodelist;

enum {NO, YES} value;

int count;	// 추가된 정점 수
int menu;	
    // 0: 정점 추가 1: 폴리곤-드래그  2: 폴리곤 회전, 이동

int drag_selected;									// 폴리곤 선택 여부
int drag_startx, drag_starty, drag_endx, drag_endy;	// 드래그 박스 크기
int drag_now;										// 드래그 여부

int center_x, center_y;	// 폴리곤 중점
float spin;				// 각도

int mouse_x, mouse_y;			// 현재 마우스 위치 - motion
int window_w=400, window_h=400;	// 윈도우 창 크기

void init(){
	glClearColor(0.0, 0.0, 0.0, 0.0);
	glShadeModel(GL_FLAT);
	glMatrixMode(GL_PROJECTION);
	glOrtho(0.0, window_w, -window_h, 0.0, -1.0, 1.0);
	glClear(GL_COLOR_BUFFER_BIT);

	count=0;
	menu=0;
	drag_selected=NO;
	drag_now=NO;
	nodelist.head=NULL;
	nodelist.tail=NULL;
}

// 정점 추가
void insert_node(int x, int y){
	node *new_node;
	new_node=(node*)malloc(sizeof(node));	// 동적 메모리 할당
	new_node->x = x;	new_node->y = y;
	new_node->num=count++;		// 0~

	if(nodelist.head==NULL){	// header, tail 설정
		nodelist.head=new_node;
		nodelist.tail=new_node;
	}
	else{	// 마지막 노드로 설정
		nodelist.tail->link=new_node;
		nodelist.tail=new_node;
	}
	new_node->link=NULL;

	glutPostRedisplay();
}

// 숫자 텍스트 출력
void text_output(node *p){
	char *c;
	char string[10];
	itoa(p->num, string, 10);	// 정점 번호 문자열로 입력 받기

	glRasterPos2f(p->x, -(p->y-5));
	for(c=string; *c !='\0'; c++){
		glutBitmapCharacter(GLUT_BITMAP_8_BY_13, *c);	// 윈도우에 출력
	}
}

// 드래그 박스 정보 입력
void dragbox(int x, int y){
	drag_endx=x;
	drag_endy=y;

	glutPostRedisplay();
}

// 드래그 영역 확인
int drag_check(){
	int a;
	int x1, x2, y1, y2;
	node *p;
	a=YES;
	
	// 드래그 영역에 정점이 모두 포함되는지 확인
	p=nodelist.head;
	if(drag_startx < drag_endx){	x1=drag_startx;	x2=drag_endx;}
	else{							x1=drag_endx;	x2=drag_startx;}
	if(drag_starty < drag_endy){	y1=drag_starty;	y2=drag_endy;}
	else{							y1=drag_endy;	y2=drag_starty;}

	while(p){	// 드래그 박스에 포함되지 않는 정점 여부
		if(p->x < x1 || p->x > x2 || p->y < y1 || p->y > y2){
			a=NO;
			break;
		}
		p=p->link;
	}

	return a;
}

void motion(int x, int y){
	mouse_x=x;
	mouse_y=y;

	glutPostRedisplay();
}

void spinObject(){
	spin += 0.01;
	if(spin>360.0) spin = spin - 360.0;

	glutPostRedisplay();
}

void moveView(int x, int y){
	glMatrixMode(GL_MODELVIEW);
	glViewport(x-center_x, -y+center_y, window_w, window_h);

	glutPostRedisplay();
}

// 초기화
void reset(){
	node *p, *next;
	p=nodelist.head;
	while(p){
		next=p->link;
		free(p);
		p=next;
	}

	nodelist.head=NULL;
	nodelist.tail=NULL;
	menu=0;
	count=0;
	drag_selected=NO;
	drag_now=NO;
	spin=0;

	glutIdleFunc(NULL);
	glutPassiveMotionFunc(motion);
	glMatrixMode(GL_PROJECTION);
	glViewport(0.0, 0.0, window_w, window_h);

	glutPostRedisplay();
}

void mouse(int button, int state, int x, int y){
	node *p;
	switch(button){
	// 왼쪽 마우스 클릭
	case GLUT_LEFT_BUTTON:
		// 정점 추가
		if(menu==0 && state==GLUT_DOWN){
			insert_node(x, y);
		}
		// 드래그 시작
		else if((menu==1) && state==GLUT_DOWN){
			drag_startx=x;
			drag_starty=y;
			drag_selected=NO;
			drag_now=YES;
			glutMotionFunc(dragbox);
		}
		// 드래그 종료
		else if(menu==1 && state==GLUT_UP){
			drag_endx=x;
			drag_endy=y;
			drag_now=NO;
			glutMotionFunc(NULL);
			drag_selected = drag_check();

			// 드래그 영역 포함 X
			if(drag_selected == NO){
				menu=1;	// 폴리곤-드래그
			}

			// 드래그 영역 포함 O
			else{	
				menu=2;	// 폴리곤 회전-이동

				// 폴리곤 중점 계산
				center_x=0;
				center_y=0;
				p=nodelist.head;
				while(p){
					center_x+=p->x;
					center_y+=p->y;
					p=p->link;
				}
				center_x /= count;
				center_y /= count;

				// 폴리곤 회전-이동
				glutIdleFunc(spinObject);
				glutPassiveMotionFunc(moveView);
			}
		}
		break; 
		
	// 오른쪽 마우스 클릭
	case GLUT_RIGHT_BUTTON:
		// 폴리곤 생성
		if(state==GLUT_DOWN){
			menu=1;	// 폴리곤-드래그
			glutPostRedisplay();
		}
		break;

	// 가운데 마우스 클릭
	case GLUT_MIDDLE_BUTTON:
		// 초기화
		reset();
		break;
	}
}

void display(){
	node *p;

	glClear(GL_COLOR_BUFFER_BIT);

	glPushMatrix();

		// 회전
		glTranslatef(center_x, -center_y, 0.0);
		glRotatef(spin, 0.0, 0.0, 1.0);
		glTranslatef(-center_x, center_y, 0.0);

	// 폴리곤
	if(menu != 0){
		if(drag_selected == NO)		// 기본 → dark yellow
			glColor3f((GLfloat)214/255, (GLfloat)201/255, (GLfloat)89/255);
		else	// 선택 폴리곤 → yellow
			glColor3f((GLfloat)255/255, (GLfloat)228/255, (GLfloat)0/255);
		
		p=nodelist.head;
		glBegin(GL_POLYGON);
			while(p){
				glVertex2f(p->x, -(p->y));
				p=p->link;
			}
		glEnd();
	}

	glColor3f(0.0, 1.0, 0.0);	// Green: 정점, 숫자

	// 정점
	glPointSize(3.0);
	p=nodelist.head;
	glBegin(GL_POINTS);
		while(p){
			glVertex2f(p->x, -(p->y));
			p=p->link;
		}
	glEnd();

	// 숫자
	p=nodelist.head;
	while(p){
		text_output(p);
		p=p->link;
	}
	
	// 간선 + Rubber Banding
	glColor3f(1.0, 1.0, 1.0);	// White
	p=nodelist.head;
	if(menu==0)	// 정점 추가 - 간선
		glBegin(GL_LINE_STRIP);
	else		// 폴리곤 완성(테두리표시)
		glBegin(GL_LINE_LOOP);
		while(p){
			glVertex2f(p->x, -(p->y));
			p=p->link;
		}
		if(menu==0)	glVertex2f(mouse_x, -mouse_y);	// 정점 추가 - Rubber Banding
	glEnd();

	// 드래그 박스
	if(drag_now == YES){
		glColor3f((GLfloat)124/255, (GLfloat)181/255, (GLfloat)190/255);	// sky blue
		glBegin(GL_LINE_LOOP);
			glVertex2f(drag_startx, -drag_starty);
			glVertex2f(drag_startx, -drag_endy);
			glVertex2f(drag_endx, -drag_endy);
			glVertex2f(drag_endx, -drag_starty);
		glEnd();
	}

	glPopMatrix();
	
	glutSwapBuffers();
}

void reshape(int new_w, int new_h){
	glLoadIdentity();
	window_w = new_w;
	window_h = new_h;
	glOrtho(0.0, window_w, -window_h, 0.0, 1.0, -1.0);
	glViewport(0, 0, window_w, window_h);
}

int main(int argc, char** argv){
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB);
	glutInitWindowPosition(10, 10);
	glutInitWindowSize(window_w, window_h);
	glutCreateWindow("20194056 이선아 - Computer Graphics");
	
	init();
	glutDisplayFunc(display);
	glutReshapeFunc(reshape);
	glutMouseFunc(mouse);
	glutPassiveMotionFunc(motion);

	glutMainLoop();
	return 0;
}
