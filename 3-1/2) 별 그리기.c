// 컴퓨터 그래픽스 : 2. 별 그리기.c

#include <GL/glut.h>
#include <stdlib.h>
#include <stdio.h>

#define MAX 100

int right_mouse_click = 1;		// 회전 방향
static GLfloat spin = 0.0;		// 회전 각도

int window_w = 400, window_h = 300;		// WIndow 창 크기

typedef struct element{
	GLfloat r, g, b;	// Color
	int x, y;			// Position
} ELEMENT;
ELEMENT element[MAX] = {0};
unsigned int count = 0;	// Star 개수 및 인덱스

void init(){
	glClearColor(0.0, 0.0, 0.0, 0.0);	// Window Background Color: Black
	glShadeModel(GL_FLAT);
	glOrtho(0.0, window_w, -window_h, 0.0, -1.0, 1.0);
	glClear(GL_COLOR_BUFFER_BIT);
}

// Left Mouse Click: Draw a new star
void drawStar(int x, int y){
	int r, g, b;
	r = rand() % 256;	// Get random Colors
	g = rand() % 256;
	b = rand() % 256;

	// Save the information of a new star
	element[count].r = (GLfloat)r / 255;
	element[count].g = (GLfloat)g / 255;
	element[count].b = (GLfloat)b / 255;
	element[count].x = x;
	element[count++].y = -y;

	glutPostRedisplay();	// Display Callback
}

// Middle Mouse Click: Change the color of the stars
void changeColor(){
	int i, r, g, b;
	for(i=0; i<count; i++){
		r = rand() % 256;	// Get random Colors
		g = rand() % 256;
		b = rand() % 256;
		element[i].r = (GLfloat) r / 255;	// Save the new radom Colors
		element[i].g = (GLfloat) g / 255;
		element[i].b = (GLfloat) b / 255;
	}
	glutPostRedisplay();	// Display Callback
}

// Right Mouse Click: Rotate the stars
void spinStar(){
	spin += 0.1 * right_mouse_click;	// Calculate angle value
	if(spin > 360.0) spin = spin - 360.0;
	else if(spin < 0.0) spin = spin + 360.0;

	glutPostRedisplay();	// Display Callback
}

void mouse(int button, int state, int x, int y){
	switch(button){
	// Left Mouse Click: Draw a new star
	case GLUT_LEFT_BUTTON:
		if(state == GLUT_DOWN && count < MAX){
			drawStar(x, y);
		}
		else if(state == GLUT_DOWN && count >= MAX){	// count :: MAX Over
			printf("\n\n");
			printf("------------------< 안내 >------------------\n");
			printf("       더 이상 별을 그릴 수 없습니다.       \n");
			printf("     그 외의 기능은 계속 이용 가능합니다.   \n");
			printf("--------------------------------------------\n\n\n");
		}
		break;
	
	// Right Mouse Click: Rotate the stars
	case GLUT_RIGHT_BUTTON:
		if(state == GLUT_DOWN){
			right_mouse_click *= -1;	// Change 
			glutIdleFunc(spinStar);
		}
		break;

	// Middle Mouse Click: Change the stars' color
	case GLUT_MIDDLE_BUTTON:
		if(state == GLUT_DOWN){
			changeColor();
		}
		break;

	default:
		break;
	}
}

void keyboard(unsigned char key, int x, int y){
	if(key == ' ')	glutIdleFunc(NULL);		// Stop Spining
}

void display(){
	int i, j;
	glClear(GL_COLOR_BUFFER_BIT);	// clear screan

	for(i=0; i<count; i++){		// draw all star
		glPushMatrix();
		// set the angle
		glTranslatef(element[i].x, element[i].y, 0.0);
		glRotatef(spin, 0.0, 0.0, 1.0);
		glTranslatef(-element[i].x, -element[i].y, 0.0);
		glColor3f(element[i].r,element[i].g, element[i].b);	// color
		glBegin(GL_TRIANGLES);	// draw
			glVertex2f(0.0 + element[i].x, 20.0 + element[i].y);
			glVertex2f(-20.0 + element[i].x, -10.0 + element[i].y);
			glVertex2f(20.0 + element[i].x, -10.0 + element[i].y);
			glVertex2f(0.0 + element[i].x, -20.0 + element[i].y);
			glVertex2f(-20.0 + element[i].x, 10.0 + element[i].y);
			glVertex2f(20.0 + element[i].x, 10.0 + element[i].y);
		glEnd();
		glPopMatrix();
	}

	if(i>0){
		glColor3f(1.0, 1.0, 1.0);	// color: white
		glBegin(GL_LINE_STRIP);	// draw lines
			for(j=0; j<count; j++)
				glVertex2f(element[j].x, element[j].y);
			glEnd();
	}

	glutSwapBuffers();
}

void reshape(int new_w, int new_h){
	glViewport(0, 0, new_w, new_h);
	glLoadIdentity();
	window_w = new_w;
	window_h = new_h;
	glOrtho(0.0, window_w, -window_h, 0.0, 1.0, -1.0);	// 사이즈 재설정
}

int main(int argc, char** argv){
	glutInit(&argc, argv);

	// Window Setting
	glutInitWindowPosition(10, 10);
	glutInitWindowSize(window_w, window_h);
	glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB);
	glutCreateWindow("20194056 이선아 - HW2");

	init();
	glutDisplayFunc(display);
	glutReshapeFunc(reshape);
	glutMouseFunc(mouse);
	glutKeyboardFunc(keyboard);
	glutMainLoop();
	return 0;
}
