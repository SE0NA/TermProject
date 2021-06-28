#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <iostream>
#include <vector>
#include <GL/glut.h>
#include "lodepng.h"

typedef struct VERTEX{
	GLfloat x, y, z;
} VERTEX;
typedef struct FACE{
	int n1, n2, n3;
} FACE;

GLuint textureID[2];

float cam_move[2] = {0.0, 0.0};	// CAM 1234 * 좌우, 줌
float cam4_angle = 0.0;				// CAM 4 * 회전

int vertexNum = 0;		// vertex 수
int normalNum = 0;		// normal 수
int texcoordNum = 0;	// texture 수
int faceNum = 0;		// face 수

VERTEX *vertexData;		// Obj의 vertex 정보 저장
VERTEX *normalData;		// Obj의 noraml vector 정보 저장
VERTEX *texcoordData;	// Obj의 texture 좌표 저장
FACE *faceData;			// Obj의 vertex 순서 저장
FACE *normalfaceData;	// Obj의 noraml 순서 저장
FACE *texfaceData;		// Obj의 texture 순서 저장
VERTEX v_AVG, v_MAX, v_MIN;		// Obj의 좌표 정보

static int width;				// 윈도우 크기
static int height;
unsigned int iwidth, iheight;	// 이미지 크기

GLboolean isTexture = false;	// 텍스쳐 선택

void init();
void ReadObj();
void RenderObj();
void loadTexture(GLuint*, char*);
void FreeMem();
void material(float, float, float, float, float, float, float, float, float, float);
void display();
void keyboard(int, int, int);
void reshape(int, int);
void MainMenu(int);
void SubMenu1(int);
void SubMenu2(int);

void init(){
	GLfloat light_position0[] = {50.0, 80.0, -30.0, 1.0};
	GLfloat light_specular0[] = {1.0, 1.0, 1.0, 1.0};	
	GLfloat light_diffuse0[] = {0.7, 0.7, 0.7, 1.0};
	GLfloat light_ambient0[] = {0.3, 0.3, 0.3, 1.0};	
	
	GLfloat light_position1[] = {-70.0, 5.0, -10.0, 0.0};
	GLfloat light_specular1[] = {1.0, 1.0, 1.0, 1.0};
	GLfloat light_diffuse1[] = {0.4, 0.4, 0.4, 1.0};
	GLfloat light_ambient1[] = {0.0, 0.0, 0.0, 1.0};

	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	// LIGHT0
	glLightfv(GL_LIGHT0, GL_POSITION, light_position0);
	glLightfv(GL_LIGHT0, GL_DIFFUSE, light_diffuse0);	
	glLightfv(GL_LIGHT0, GL_SPECULAR, light_specular0);	
	glLightfv(GL_LIGHT0, GL_AMBIENT, light_ambient0);
	// LIGH1
	glLightfv(GL_LIGHT1, GL_POSITION, light_position1);
	glLightfv(GL_LIGHT1, GL_DIFFUSE, light_diffuse1);	
	glLightfv(GL_LIGHT1, GL_SPECULAR, light_specular1);	
	glLightfv(GL_LIGHT1, GL_AMBIENT, light_ambient1);

	glEnable(GL_LIGHTING);
	glEnable(GL_LIGHT0);
	glEnable(GL_LIGHT1);
	glEnable(GL_DEPTH_TEST);
	glEnable(GL_CULL_FACE);
	glFrontFace(GL_CW);

	glOrtho(-100.0, 100.0, -100.0, 100.0, -100.0, 100.0);

	glClearColor(0.0, 0.0, 0.0, 1.0);
    glShadeModel(GL_SMOOTH);

	loadTexture(&textureID[0], "dino_basic.png");
	loadTexture(&textureID[1], "dino_rainbow.png");
	ReadObj();

	cam4_angle = 0.0;

	material(0.2, 0.2, 0.2, 0.6, 0.6, 0.6, 1.0, 1.0, 1.0, 100.0);
}

void ReadObj(){
	FILE *fp;
	int index_v = 0, index_f = 0, index_n = 0, index_t = 0;
	char *token, tmp[256];
	int i;
	FACE f, n, t;

	fp = fopen("dino.obj", "r");
	if(fp==NULL){
		printf("File Not Found.\n\n");
		exit(1);
	}

	// 개수 카운팅
	while(!feof(fp)){	
		fscanf(fp, "%s", tmp);	
		if(tmp[0] == 'v'){
			if(tmp[1] == '\0')		vertexNum++;		// 정점
			else if(tmp[1] == 'n')	normalNum++;		// 노말 벡터
			else if(tmp[1] == 't')	texcoordNum++;		// 텍스처 
		}
		else if(tmp[0] == 'f' && tmp[1] == '\0')		faceNum++;	// 면 수
	}

	printf("vertexNum	: %d\n", vertexNum);
	printf("normalNum	: %d\n", normalNum);
	printf("texcoordNum : %d\n", texcoordNum);
	printf("faceNum		: %d\n\n", faceNum);

	// 메모리 할당
	vertexData = (VERTEX*)malloc(sizeof(VERTEX)*vertexNum);
	normalData = (VERTEX*)malloc(sizeof(VERTEX)*normalNum);
	texcoordData = (VERTEX*)malloc(sizeof(VERTEX)*texcoordNum);
	faceData = (FACE*)malloc(sizeof(FACE)*faceNum);
	normalfaceData = (FACE*)malloc(sizeof(FACE)*faceNum);
	texfaceData = (FACE*)malloc(sizeof(FACE)*faceNum);

	// Obj 정보 저장
	v_MAX.x = -100.0;	v_MAX.y = -100.0;	v_MAX.z = -100.0;
	v_MIN.x = 100.0;	v_MIN.y = 100.0;	v_MIN.z = 100.0;
	v_AVG.x = 0.0;		v_AVG.y = 0.0;		v_AVG.z = 0.0;

	rewind(fp);
	while(!feof(fp)){
		fgets(tmp, 256, fp);
		if(tmp[0] == 'v'){
			if(tmp[1] == 't'){		// vt 좌표 저장
				fseek(fp, -(strlen(tmp)+1), SEEK_CUR);
				fscanf(fp, "%s %f %f %f", tmp,
					&texcoordData[index_t].x, &texcoordData[index_t].y, &texcoordData[index_t].z);
				index_t++;
			}
			else if(tmp[1] == 'n'){	// vn 벡터 저장
				fseek(fp, -(strlen(tmp)+1), SEEK_CUR);
				fscanf(fp, "%s %f %f %f", tmp,
					&normalData[index_n].x, &normalData[index_n].z, &normalData[index_n].y);
				index_n++;
			}
			else {					// v 좌표 저장
				fseek(fp, -(strlen(tmp)+1), SEEK_CUR);
				fscanf(fp, "%s %f %f %f", tmp,
					&vertexData[index_v].x, &vertexData[index_v].z, &vertexData[index_v].y);
	
				if(vertexData[index_v].x > v_MAX.x)		v_MAX.x = vertexData[index_v].x;
				if(vertexData[index_v].y > v_MAX.y)		v_MAX.y = vertexData[index_v].y;
				if(vertexData[index_v].z > v_MAX.z)		v_MAX.z = vertexData[index_v].z;
				if(vertexData[index_v].x < v_MIN.x)		v_MIN.x = vertexData[index_v].x;
				if(vertexData[index_v].y < v_MIN.y)		v_MIN.y = vertexData[index_v].y;
				if(vertexData[index_v].z < v_MIN.z)		v_MIN.z = vertexData[index_v].z;
				index_v++;
			}
		}
		else if(tmp[0] == 'f'){			// face 정보(v/t/n)
			for(i=0; i<strlen(tmp); ++i){
					if(tmp[i] == '/')	tmp[i] = ' ';
				}
				token = strtok(tmp, " ");

				// p: 면의 정점 순서			// t: texcoord vector 순서		// n: normal vector 순서
				f.n1 = atoi(strtok(NULL, " "));	t.n1 = atoi(strtok(NULL, " "));	n.n1 = atoi(strtok(NULL, " "));
				f.n2 = atoi(strtok(NULL, " "));	t.n2 = atoi(strtok(NULL, " "));	n.n2 = atoi(strtok(NULL, " "));
				f.n3 = atoi(strtok(NULL, " "));	t.n3 = atoi(strtok(NULL, " "));	n.n3 = atoi(strtok(NULL, " "));

				faceData[index_f].n1	= f.n1-1;		faceData[index_f].n2 = f.n2-1;			faceData[index_f].n3 = f.n3-1;
				texfaceData[index_f].n1 = t.n1-1;		texfaceData[index_f].n2 = t.n2-1;		texfaceData[index_f].n3 = t.n3-1;
				normalfaceData[index_f].n1 = n.n1-1;	normalfaceData[index_f].n2 = n.n2-1;	normalfaceData[index_f].n3 = n.n3-1;
				index_f++;
		}
	}
	fclose(fp);
	
	v_AVG.x = (v_MAX.x+v_MIN.x)/2;
	v_AVG.y = (v_MAX.y+v_MIN.y)/2;
	v_AVG.z = (v_MAX.z+v_MIN.z)/2;
}

void RenderObj(){
	int i;
	VERTEX v[3], n[3], t[3];

	glPushMatrix();

	for(i=0; i<faceNum; i++){
		// vertex
		v[0].x = vertexData[faceData[i].n1].x;			v[0].y = vertexData[faceData[i].n1].y;			v[0].z = vertexData[faceData[i].n1].z;
		v[1].x = vertexData[faceData[i].n2].x;			v[1].y = vertexData[faceData[i].n2].y;			v[1].z = vertexData[faceData[i].n2].z;
		v[2].x = vertexData[faceData[i].n3].x;			v[2].y = vertexData[faceData[i].n3].y;			v[2].z = vertexData[faceData[i].n3].z;
		// normal
		n[0].x = normalData[normalfaceData[i].n1].x;	n[0].y = normalData[normalfaceData[i].n1].y;	n[0].z = normalData[normalfaceData[i].n1].z;
		n[1].x = normalData[normalfaceData[i].n2].x;	n[1].y = normalData[normalfaceData[i].n2].y;	n[1].z = normalData[normalfaceData[i].n2].z;
		n[2].x = normalData[normalfaceData[i].n3].x;	n[2].y = normalData[normalfaceData[i].n3].y;	n[2].z = normalData[normalfaceData[i].n3].z;
		// texcoord
		t[0].x = texcoordData[texfaceData[i].n1].x;		t[0].y = iheight - texcoordData[texfaceData[i].n1].y;	
		t[1].x = texcoordData[texfaceData[i].n2].x;		t[1].y = iheight - texcoordData[texfaceData[i].n2].y;	
		t[2].x = texcoordData[texfaceData[i].n3].x;		t[2].y = iheight - texcoordData[texfaceData[i].n3].y;

		glBegin(GL_TRIANGLES);
			if(!isTexture){		// 단색
				glNormal3f(n[0].x, n[0].y, n[0].z);
				glVertex3f(v[0].x, v[0].y, v[0].z);
				glNormal3f(n[1].x, n[1].y, n[1].z);
				glVertex3f(v[1].x, v[1].y, v[1].z);
				glNormal3f(n[2].x, n[2].y, n[2].z);
				glVertex3f(v[2].x, v[2].y, v[2].z);
			}
			else{				// 텍스쳐
				glNormal3f(n[0].x, n[0].y, n[0].z);
				glTexCoord2f(t[0].x, t[0].y);
				glVertex3f(v[0].x, v[0].y, v[0].z);
				glNormal3f(n[1].x, n[1].y, n[1].z);
				glTexCoord2f(t[1].x, t[1].y);
				glVertex3f(v[1].x, v[1].y, v[1].z);
				glNormal3f(n[2].x, n[2].y, n[2].z);
				glTexCoord2f(t[2].x, t[2].y);
				glVertex3f(v[2].x, v[2].y, v[2].z);
			}
		glEnd();
	}
	glPopMatrix();
}

void loadTexture(GLuint *texture, char *path){
	std::vector<unsigned char> image, image2;

	size_t u2, v2, c, x, y;
	unsigned error = lodepng::decode(image, iwidth, iheight, path);
	if(!error)
		std::cout<<"error"<<error<<": "<<lodepng_error_text(error)<<std::endl;
	u2 = 1;	while(u2 < iwidth)	u2 *= 2;
	v2 = 1;	while(v2 < iheight)	v2 *= 2;
	image2 = std::vector<unsigned char>(u2 * v2 * 4);
	for(y=0; y<iheight; y++)
		for(x=0; x<iwidth; x++)
			for(c=0; c<4; c++)
				image2[4*u2*y+4*x+c] = image[4*iwidth*y+4*x+c];

	glGenTextures(1, texture);
	glBindTexture(GL_TEXTURE_2D, *texture);
	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, u2, v2, 0, GL_RGBA, GL_UNSIGNED_BYTE, &image2[0]);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
}

void FreeMem(){
	free(vertexData);
	free(normalData);
	free(texcoordData);
	free(faceData);
	free(normalfaceData);
	free(texfaceData);
}

void material(float ambr, float ambg, float ambb,
			  float difr, float difg, float difb,
			  float specr, float specg, float specb, float shine){
	float mat_amb[] = {ambr, ambg, ambb, 1.0};
	float mat_dif[] = {difr, difg, difb, 1.0};
	float mat_spec[] = {specr, specg, specb, 1.0};

	glMaterialfv(GL_FRONT, GL_AMBIENT, mat_amb);
	glMaterialfv(GL_FRONT, GL_DIFFUSE, mat_dif);
	glMaterialfv(GL_FRONT, GL_SPECULAR, mat_spec);
	glMaterialf(GL_FRONT, GL_SHININESS, shine);
}

void display() {
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	
	glPushMatrix();

	// VIEW 1: 위
	glPushMatrix();
    glViewport(0, height/2, width/2, height/2);
    glLoadIdentity();
	gluLookAt(cam_move[0], (v_MAX.y/1.7)+cam_move[1], 0.0, cam_move[0], 0.0, 0.0, 0.0, 0.0, 1.0);
    glTranslatef(-v_AVG.x, -v_AVG.y, -v_AVG.z);
	RenderObj();
	glPopMatrix();

	// VIEW 2: 정면
	glPushMatrix();
    glViewport(width/2, height/2, width/2, height/2);
    glLoadIdentity();
	gluLookAt(cam_move[0], 0.0, v_MIN.z-cam_move[1]-50.0, cam_move[0], 0.0, 0.0, 0.0, 1.0, 0.0);
    glTranslatef(-v_AVG.x, -v_AVG.y, -v_AVG.z);
	RenderObj();
	glPopMatrix();

	// VIEW 3: 앞면
	glPushMatrix();
	glViewport(0, 0, width/2, height/2);
    glLoadIdentity();
	gluLookAt((v_MIN.x-v_AVG.x)*2-cam_move[1], 0.0, -cam_move[0], 0.0, 0.0, -cam_move[0], 0.0, 1.0, 0.0);
	glTranslatef(-v_AVG.x, -v_AVG.y, -v_AVG.z);
    RenderObj();
	glPopMatrix();
	
	// VIEW 4: 전체
	glPushMatrix();
	glViewport(width/2, 0, width/2, height/2);
    glLoadIdentity();
	gluLookAt(-50-cam_move[1], 50+cam_move[1], -50-cam_move[1],
				0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
	glTranslatef(-cam_move[0], 0.0, cam_move[0]);
	glRotatef(cam4_angle, 0.0, 1.0, 0.0);
	glTranslatef(-v_AVG.x, -v_AVG.y, -v_AVG.z);
    RenderObj();
	glPopMatrix();

    glFlush();
}

void keyboard(int key, int x, int y){
	switch(key){
	case GLUT_KEY_LEFT:		// ←
		if(cam_move[0] > -30.0)		cam_move[0] -= 3.0;
		break;

	case GLUT_KEY_RIGHT:	// →
		if(cam_move[0] < 30.0)		cam_move[0] += 3.0;
		break;

	case GLUT_KEY_UP:		// ↑
		if(cam_move[1] > -30.0)		cam_move[1] -= 3.0;
		break;

	case GLUT_KEY_DOWN:		// ↓
		if(cam_move[1] < 50.0)	cam_move[1] += 5.0;
		break;

	case GLUT_KEY_F1:		// F1
		cam4_angle +=15.0;
		if(cam4_angle > 360.0)	cam4_angle -= 360.0;
		break;
	default:
		break;
	}
	glutPostRedisplay();
}


void reshape(int w, int h) {
    width = w;
    height = h;
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glFrustum(-1.0, 1.0, -1.0, 1.0, 1.5, 500.0);
    glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}

void MainMenu(int entryID){
	if(entryID == 1){
		glDisable(GL_TEXTURE_2D);
		material(0.2, 0.2, 0.2, 0.6, 0.6, 0.6, 1.0, 1.0, 1.0, 100.0);
		glutPostRedisplay();
	}
	else if(entryID == 2){
		FreeMem();
		exit(0);
	}
}

void SubMenu1(int entryID){
	if(entryID == 1){		// Red
		isTexture = false;
		glDisable(GL_TEXTURE_2D);
		material(0.35, 0.05, 0.24, 0.81, 0.42, 0.42, 1.0, 0.86, 0.85, 50.0);
	}
	else if(entryID == 2){	// Yellow
		isTexture = false;
		glDisable(GL_TEXTURE_2D);
		material(0.31, 0.33, 0.08, 1.0, 0.87, 0.32, 1.0, 0.95, 0.74, 50.0);
	}
	else if(entryID == 3){	// Green
		isTexture = false;
		glDisable(GL_TEXTURE_2D);
		material(0.03, 0.23, 0.2, 0.44, 0.56, 0.35, 0.9, 1.0, 0.69, 50.0);
	}
	else if(entryID == 4){	// Blue
		isTexture = false;
		glDisable(GL_TEXTURE_2D);
		material(0.168, 0.074, 0.38, 0.47, 0.55, 0.9, 0.8, 0.9, 1.0, 50.0);
	}
	glutPostRedisplay();
}

void SubMenu2(int entryID){
	isTexture = true;
	material(0.3, 0.3, 0.3, 0.8, 0.8, 0.8, 1.0, 1.0, 1.0, 50.0);
	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, textureID[entryID-1]);
	glutPostRedisplay();
}

int main(int argc, char** argv) {
	GLint SubMenuID1, SubMenuID2, MainMenuID;

    glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB | GLUT_DEPTH);
    glutInitWindowSize(800, 800);
    glutInitWindowPosition(100, 100);
    glutCreateWindow("20194056 이선아 - Final Project");

	init();
    glutDisplayFunc(display);
	glutSpecialFunc(keyboard);
    glutReshapeFunc(reshape);

	// 색상 선택
	SubMenuID1 = glutCreateMenu(SubMenu1);
	glutAddMenuEntry("Red", 1);
	glutAddMenuEntry("Yellow", 2);
	glutAddMenuEntry("Green", 3);
	glutAddMenuEntry("Blue", 4);
	
	// 텍스쳐 선택
	SubMenuID2 = glutCreateMenu(SubMenu2);
	glutAddMenuEntry("Basic", 1);
	glutAddMenuEntry("Rainbow", 2);

	MainMenuID =  glutCreateMenu(MainMenu);
	glutAddSubMenu("Color", SubMenuID1);
	glutAddSubMenu("Texture", SubMenuID2);
	glutAddMenuEntry("Reset", 1);
	glutAddMenuEntry("Exit", 2);
	glutAttachMenu(GLUT_RIGHT_BUTTON);

    glutMainLoop();
    return 0;
}
