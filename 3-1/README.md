### 컴퓨터 그래픽스


***

#### ✨ 1. 3D 모델링 ✨

- 3D Studio Max를 이용하여 본인이 원하는 멋진 모형을 삼각형 메쉬를 이용하여 3D로 모델링 할 것
- 모델링한 결과를 obj 파일 형식으로 저장할 것

![image](https://user-images.githubusercontent.com/85846475/122613314-9b6ee880-d0bf-11eb-8335-a7265dc9ccbd.png)



***

#### ✨ 2. 별 그리기 ✨

- 윈도우 상에 삼각형 두 개를 사용하여 작은 별을 하나 그리시오
- 마우스 이벤트를 이용하여 왼쪽 마우스 클릭 시에 클릭된 위치로 별이 추가되면서 별의 중점을 연결하는 선을 그리시오
- 모든 별이 계속 추가되어야 함
- 마우스 이벤트를 이용하여 오른쪽 마우스 클릭 시에 모든 별이 회전하고 한 번 더 클릭하면 토글되어 모든 별이 반대로 회전되도록 만드시오
- 오른쪽 마우스 클릭마다 계속 토글
- 마우스 이벤트를 이용하여 중간 마우스 클릭 시에 모든 별의 색이 바뀌도록 만드시오

![image](https://user-images.githubusercontent.com/85846475/122613634-251eb600-d0c0-11eb-916f-04d1ba4bbfcd.png)


***

#### ✨ 3. 회전 폴리곤 ✨

- Interactive하게 폴리곤을 그리는 프로그램을 작성하라
- 사용자가 매번 왼쪽 마우스 버튼을 누를 때마다 점들이 추가되어야 한다
- 오른쪽 마우스 버튼을 누르면 하나의 색으로 칠해진 폴리곤을 만든다
- 매번 마우스 클릭으로 점들이 추가될 때마다 점들이 보여야 하고, 점의 위에는 해당 숫자가 나와야 한다
- 처음 점이 0부터 시작해서 순서대로 증가한다
- 마지막 점으로부터 현재 점까지의 라인은 rubber band line으로 표현되어야 한다
- motion callback function, glutPostRedisplay function을 활용할 것
- 물체 전체를 드래그하면 마우스의 이동에 따라서 물체가 회전하면서 이동되도록 구현
- middle 버튼을 누르면 초기화

![image](https://user-images.githubusercontent.com/85846475/122614339-6d8aa380-d0c1-11eb-91ac-d5eca993a977.png)


***

#### ✨ 4. 로봇 팔 ✨

- 팔 관절과 손가락(3개)를 가지는 robot arm을 표현하는 프로그램을 작성하라
- 각 관절은 사람의 팔처럼 일정한 각도 이상을 움직일 수 없음
- pop-up menu를 이용하여 각 관절 선택
- 'u' key는 회전각 증가, 'd'는 회전각 감소
- glPushMatrix()와 glPopMatrix()를 적절히 사용
- 's' key는 본인만의 특별한 행동을 창조적으로 추가할 것

![image](https://user-images.githubusercontent.com/85846475/122614597-ed187280-d0c1-11eb-8cc5-fbafbedc6310.png)


***

#### ✨ 5. obj 뷰어 ✨

- 본인이 만들었던 3D 물체의 obj 파일을 읽어온 후 아래와 같은 기능의 메뉴를 이용하여 제공하는 프로그램을 작성하라
- 파일 열기: 3D 물체 obj
- 카메라 이동: keyboard 화살표 키 활용(줌-인, 줌-아웃, 좌, 우)
- 조명 설치
- 색 반영
- Texture 적용

![image](https://user-images.githubusercontent.com/85846475/122614825-57c9ae00-d0c2-11eb-9176-9e73a56e521f.png)





