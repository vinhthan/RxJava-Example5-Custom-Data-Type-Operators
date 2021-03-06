package com.example.example5customdatatypeoperators;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compositeDisposable = new CompositeDisposable();


        compositeDisposable.add(studentObservable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(new Function<Student, Student>() {
            @Override
            public Student apply(Student student) throws Exception {
                student.setName(student.getName().toUpperCase());
                student.setAddress(student.getAddress().toUpperCase());
                return student;
            }
        })
        .subscribeWith(studentObserver()));
    }

    //
    private DisposableObserver<Student> studentObserver(){
        return new DisposableObserver<Student>() {
            @Override
            public void onNext(Student student) {
                Log.d(TAG, "onNext: "+student.getName() + " - "+student.getAddress());
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: "+e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        };
    }

    //
    private Observable<Student> studentObservable(){
        final List<Student> students = getStudent();

        return Observable.create(new ObservableOnSubscribe<Student>() {
            @Override
            public void subscribe(ObservableEmitter<Student> emitter) throws Exception {
                for (Student student : students){
                    if (!emitter.isDisposed()){
                        emitter.onNext(student);
                    }
                }
                if (!emitter.isDisposed()){
                    emitter.onComplete();
                }
            }
        });
    }

    //tao list Student
    private List<Student> getStudent(){
        List<Student> students = new ArrayList<>();
        students.add(new Student(1, "Vinh", "27/02/1995", "Ha Dong"));
        students.add(new Student(2, "Hai", "a/a/1996", "Cau Giay"));
        students.add(new Student(3, "Hai", "b/b/1995", "Dong Da"));
        students.add(new Student(4, "Duc", "c/c/1991", "Ha Tay"));
        students.add(new Student(5, "Son", "d/d/1995", "Thanh Xuan"));
        students.add(new Student(6, "Tuan", "e/e/1997", "Gia Lam"));

        return students;
    }

    //huy dang ky Observable
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
