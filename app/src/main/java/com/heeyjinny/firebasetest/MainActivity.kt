package com.heeyjinny.firebasetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.heeyjinny.firebasetest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //뷰바인딩
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //6
    //클래스 전체에서 이용하기 위해
    //파이어베이스의 데이터베이스 전역변수 생성
    val database = Firebase.database
    //6-1
    //데이터베이스 최상위 노드에 users(테이블이름)생성
    val myRef = database.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //9
        //버튼을 클릭하면 User데이터를 생성해
        //addItem()메서드에 전달하는 코드 작성
        with(binding){
            btnPost.setOnClickListener {
                val name = editName.text.toString()
                val age = editAge.text.toString()
                val user = User(name, age)

                addItem(user)
            }
        }

        //10
        //입력한 데이터 읽어와 안드로이드 화면에 출력
        //addValueEventListener사용
        //activity_main.xml에 출력화면 추가
        //10-1
        //데이터베이스에 addValueEventListener연결
        myRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //10-2
                //데이터가 변경되었을 때
                //출력화면 textList의 목록 지우기
                binding.textList.text = ""

                //10-3
                //snapshot안에 있는 실제데이터 반복문으로 꺼내기
                //snapshot의 children프로퍼티는 복수로 존재할 수 있는
                //모든 자식 노드가 담겨있기 때문에
                //반복문을 돌면서 자식 노드를 하나씩 꺼내 쓸 수 있음
                for (item in snapshot.children){
                    //item의 value를 꺼내 User클래스로 캐스딩
                    //value가 없을 수도 있기 때문에 let스코프 함수로 처리
                    item.getValue(User::class.java)?.let {
                        binding.textList.append("${it.name} : ${it.age} \n")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        //1
        //파이어베이스의 데이터베이스 기능이
        //앱과 정상적으로 연동되었는지 확인
        //최상위 노드 message노드 생성 후
        //문자열 Hello, Firebase!!! 저장
//        val database = Firebase.database
//        val myRef = database.getReference("message")
//        myRef.setValue("Hello, Firebase!!!")

//        //2
//        //최상위 노드 bbs 추가
//        //앞서 만들었던 message노드는 그대로 유지됨...
//        val database = Firebase.database
//        val myRef = database.getReference("bbs")
//        //2-1
//        //bbs노드 아래에 2개의 자식노드(.child()) 생성하고
//        //자식노드에 값 입력(.setValue())
//        myRef.child("name").setValue("Hong")
//        myRef.child("age").setValue(25)

//        //3
//        //일회성 값 조회(리스너가 실행된 시점에 한 번만 값 조회가능)
//        //데이터베이스의 구조는
//        //노드 값의 변경 사항이 실시간으로 반영되므로
//        //리스너 형태로 값을 읽고 사용함
//        //최상위 노드의 자식노드를 가져와
//        //addOnSuccessListener, addOnFailureListener 사용
//        myRef.child("name").get().addOnSuccessListener {
//            //3-1
//            //값을 성공적으로 읽어왔을 때 노드의 값 출력
//            Log.d("파이어베이스", "name=${it.value}")
//            print(it.value)
//        }.addOnFailureListener {
//            //3-2
//            //값을 읽어오지 못했을 때 실패 메시지 전달
//            Log.d("파이어베이스", "error=${it}")
//        }

//        //4
//        //실시간 값 조회(값이 변경될 때마다 조회)
//        //리스너 형태로 값을 읽고 사용함
//        //일회성 값 조회코드와 함께 사용할 수 없음 //3 삭제
//        //addValueListener()메서드를 사용하며
//        //메서드가 2개이기 때문에 object로 만들어 사용함
//        myRef.child("name").addValueEventListener(object: ValueEventListener{
//
//            //4-1
//            //값이 변경될 때마다 매번 호출
//            //DataSnapshot: 리스너가 호출되는 순간의 데이터를
//            //사진찍듯이 그대로 저장
//            //스냅샷 기능을 이용해 데이터 저장 시
//            //유실된 데이터를 복원하거나 데이터 상태를 일정 시점으로 복원 가능함
//            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.d("파이어베이스", "name: ${snapshot.value}")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("파이어베이스", "error: ${error.message}")
//            }
//
//        })

    }//onCreate

    //7
    //User클래스 데이터를 노드에 입력하는
    //addItem()함수 생성
    fun addItem(user: User){
        //7-1
        //데이터 입력 시 키를 생성하기 위해
        //push() 메서드 사용
        //노드.push().key 호출 : 파이어베이스가 키를 생성해 변수에 반환
        val id = myRef.push().key!!
        //7-2
        //반환받은 키 값을 User클래스의 id값에 저장
        user.id = id
        //7-3
        //데이터베이스 최상위 노드 users에
        //자식노드 id를 만들고 그 값을 User클래스 값으로 생성
        //addItem(name, age)형식으로 데이터 입력 시
        //user테이블에 id값 당 name과 age값이 생성됨
        myRef.child(id).setValue(user)
    }

    //8
    //테스트를 위해
    //activity_main.xml 수정 및
    //build.gradle 뷰바인딩 설정

}//MainActivity

//5
//목록 형태로 데이터 입력하기
//관계형 데이터베이스에서 최상위 노드가 하나의 테이블과 같음
//그 아래에 입력되는 데이터는 반복되는 형태로 입력 됨

//최상위노드 아래에 각 데이터마다 아이디가 되는 자식 노드를 생성하고
//해당 아이디에 데이터를 값으로 입력해 만들 수 있음

//2번의 최상위 노드 및 조회코드 //3,4번 삭제

//5-1
//User클래스 정의
//name, age 필드와 데이터 구분용의 id필드 추가
//파이어베이스에서 사용하기 위해
//아무것도 없는 생성자도 만들어놓아야 함
class User{
    var id:String = ""
    var name:String = ""
    var age: String = ""

    //5-2
    //파이어베이스에서 데이터 변환을 위해
    //아무것도 없는 생성자 생성
    constructor()

    constructor(name: String, age: String){
        this.name = name
        this.age = age
    }

}//User