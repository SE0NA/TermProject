#include <GL/glut.h>
#include <stdlib.h>
#include <Windows.h>
#include <time.h>

// 부위별 각도
static float shoulder=0, elbow=0, hand=0, finger1=0, finger2=0, finger3=0;
enum part {NOT, S, E, H, F1, F2, F3};

int selected, special_key, sp_elbow;


void init(void){
	glClearColor(0.0, 0.0, 0.0, 0.0);
	glShadeModel(GL_FLAT);

	selected=NOT;
	special_key=0;
}

void display(void){
	glClear(GL_COLOR_BUFFER_BIT);

	glLineWidth(3.0);

	// Shoulder
	glColor3f(1.0, 0.0, 0.0);	// 빨강
	glPushMatrix();	
	glRotatef((GLfloat)shoulder, 0.0, 0.0, 1.0);
	glTranslatef(1.0, 0.0, 0.0);
	glPushMatrix();
	glScalef(2.0, 0.5, 1.0);	// Shoulder Size
	glutWireCube(1.0);
	glPopMatrix();

	// Elbow
	glColor3f(1.0, 0.7, 0.0);	// 주황
	glTranslatef(1.0, 0.0, 0.0);
	glRotatef((GLfloat)elbow, 0.0, 0.0, 1.0);
	glTranslatef(1.0, 0.0, 0.0);
	glPushMatrix();
	glScalef(2.0, 0.5, 1.0);	// Elbow Size
	glutWireCube(1.0);
	glPopMatrix();

	// Hand
	glColor3f(1.0, 1.0, 0.0);	// 노랑
	glTranslatef(1.0, 0.0, 0.0);
	glRotatef((GLfloat)hand, 0.0, 0.0, 1.0);
	glTranslatef(0.3, 0.0, 0.0);
	glPushMatrix();
	glScalef(0.6, 0.5, 1.0);	// Hand Size
	glutWireCube(1.0);
	glPopMatrix();

	// Finger1
	glPushMatrix();
	glColor3f(0.0, 1.0, 0.0);	// 초록
	glTranslatef(0.3, 0.0, -0.4);
	glRotatef((GLfloat)finger1, 0.0, 0.0, 1.0);
	glRotatef(15.0, 0.0, 1.0, 0.0);
	glTranslatef(0.3, 0.0, 0.0);
	glScalef(0.6, 0.2, 0.2);	// Finger Size
	glutWireCube(1.0);
	glPopMatrix();

	// Finger2
	glPushMatrix();
	glColor3f(0.0, 1.0, 1.0);	// 청록
	glTranslatef(0.3, 0.0, 0.0);
	glRotatef((GLfloat)finger2, 0.0, 0.0, 1.0);
	glTranslatef(0.3, 0.0, 0.0);
	glScalef(0.6, 0.2, 0.2);	// Finger Size
	glutWireCube(1.0);
	glPopMatrix();

	// Finiger3
	glPushMatrix();
	glColor3f(1.0, 0.0, 1.0);	// 보라
	glTranslatef(0.3, 0.0, 0.4);
	glRotatef((GLfloat)finger3, 0.0, 0.0, 1.0);
	glRotatef(-15.0, 0.0, 1.0, 0.0);
	glTranslatef(0.3, 0.0, 0.0);
	glScalef(0.6, 0.2, 0.2);	// Finger Size
	glutWireCube(1.0);
	glPopMatrix();

	glPopMatrix();
	glutSwapBuffers();
}

void SpecialAct(){
	static int shape, turn;

	if(sp_elbow==0){		// 팔피기
		if(elbow < 0){
			sp_elbow = 1;	turn = 0;
			shape = rand() % 3 + 1;
		}
		elbow-=0.1;
	}

	else if(sp_elbow==1){	// 손 내기
		if(turn == 0){		// turn 0: 손피기
			if(shape == 1){			// 가위
				if(finger1 < 0)		turn = 1;
				finger1 -= 0.25;	finger2 -=0.25;
			}
			else if(shape == 2){	// 바위
				Sleep(100);
				turn = 1;
			}
			else{					// 보
				if(finger1 < 0)		turn = 1;
				finger1 -= 0.25;	finger2 -= 0.25;	finger3 -= 0.25;
			}
		}
		else if(turn == 1){	// turn 1: 대기
			Sleep(300);
			turn = 2;
		}
		else{				// turn 2: 손 접기
			if(shape == 1){			// 가위
				if(finger1 > 160)	sp_elbow = 2;
				finger1 += 0.25;	finger2 += 0.25;
			}
			else if(shape == 2){	// 
				Sleep(100);
				sp_elbow = 2;
			}
			else{					// 보
				if(finger1 > 160)	sp_elbow = 2;
				finger1 += 0.25;	finger2 += 0.25;	finger3 += 0.25;
			}
		}
	}

	else{			// 팔 접기
		if(elbow > 120){
			sp_elbow = 0;	turn = 0;
		}
		elbow+=0.1;
	}
	
	glutPostRedisplay();
}
void keyboard(unsigned char key, int x, int y){
	switch(key){
	case 'u':	// 회전각 증가
		if(selected==S && shoulder<90)			shoulder += 5;
		else if(selected==E && special_key ==0 && elbow<160)		elbow += 5;
		else if(selected==H && special_key ==0 && hand<70)			hand += 5;
		else if(selected==F1 && special_key ==0 && finger1>0)		finger1 -= 5;
		else if(selected==F2 && special_key ==0 && finger2>0)		finger2 -= 5;
		else if(selected==F3 && special_key ==0 && finger3>0)		finger3 -= 5;
		break;

	case 'd':	// 회전각 감소
		if(selected==S && shoulder>-90)		shoulder -= 5;
		else if(selected==E && special_key ==0 && elbow>0)			elbow -= 5;
		else if(selected==H && special_key ==0 && hand>-70)			hand -= 5;
		else if(selected==F1 && special_key ==0 && finger1<160)		finger1 += 5;
		else if(selected==F2 && special_key ==0 && finger2<160)		finger2 += 5;
		else if(selected==F3 && special_key ==0 && finger3<160)		finger3 += 5;
		break;
		
	case 's':	// 가위 바위 보
		// special_key  0:정지, 1:시작
		special_key = ++special_key % 2;

		if(special_key % 2 == 1){
			// 팔 접은 상태에서 시작
			elbow=120; hand=0; finger1=160; finger2=160; finger3=160;
			sp_elbow=0;
			srand(time(NULL));
			glutIdleFunc(SpecialAct);
		}
		else
			glutIdleFunc(NULL);
		break;
		
	default:
		break;
	}

	glutPostRedisplay();
}

void Main_Menu(int value){
	switch(value){
	case S:	
		glutSetWindowTitle("Move Shoulder..");
		selected = S;
		break;

	case E:	
		glutSetWindowTitle("Move Elbow..");
		selected = E;
		break;

	case H:	
		glutSetWindowTitle("Move Hand..");
		selected = H;
		break;

	case 9:	// 초기화
		glutSetWindowTitle("20194056 이선아 - HW3");
		glutIdleFunc(NULL);
		selected = NOT;	special_key=0;
		shoulder=0, elbow=0, hand=0, finger1=0, finger2=0, finger3=0;
		glutPostRedisplay();
		break;

	case 10:
		exit(0);
		break;

	default:
		break;
	}
}

void Sub_Menu(int value){
	switch(value){
	case F1:	
		glutSetWindowTitle("Move Finger1..");
		selected = F1;
		break;

	case F2:	
		glutSetWindowTitle("Move Finger2..");
		selected = F2;
		break;

	case F3:	
		glutSetWindowTitle("Move Finger3..");
		selected = F3;
		break;

	default:
		break;
	}
}

void reshape(int w, int h){
	glViewport(0, 0, (GLsizei)w, (GLsizei)h);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluPerspective(65.0, (GLfloat)w/(GLfloat)h, 1.0, 20.0);
	
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();

	gluLookAt(2.5, 3.0, 5.0, 2.5, 0.0, 0.0, 0.0, 1.0, 0.0);		// 정면
//	gluLookAt(-3.0, 3.0, 3.0, 2.0, 0.0, 0.0, 0.0, 1.0, 0.0);	// 좌측
//	gluLookAt(7.0, 3.0, 3.0, 3.0, 0.0, 0.0, 0.0, 1.0, 0.0);		// 우측
}

int main(int argc, char**argv){
	GLint mainID, subID;

	glutInit(&argc, argv);
	glutInitWindowSize(600, 600);
	glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB);
	glutCreateWindow("20194056 이선아");

	init();
	// Sub Menu
	subID = glutCreateMenu(Sub_Menu);
	glutAddMenuEntry("Finger1", F1);
	glutAddMenuEntry("Finger2", F2);
	glutAddMenuEntry("Finger3", F3);

	// Main Menu
	mainID = glutCreateMenu(Main_Menu);
	glutAddMenuEntry("Shoulder", S);
	glutAddMenuEntry("Elbow", E);
	glutAddMenuEntry("Hand",H);
	glutAddSubMenu("Finger", subID);
	glutAddMenuEntry("Reset", 9);
	glutAddMenuEntry("Exit", 10);

	glutAttachMenu(GLUT_RIGHT_BUTTON);
	glutDisplayFunc(display);
	glutReshapeFunc(reshape);
	glutKeyboardFunc(keyboard);

	glutMainLoop();
	return 0;
}
