package com.example.myungwook.memorypower;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Random;

public class MemoryPower extends Activity{
    GameView gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gv = new GameView(this);
        setContentView(gv);
    }

    class Shape{
        final static int RECT = 0;
        final static int CIRCLE = 1;
        final static int TRIANGLE = 2;

        int what;
        int color;
        Rect rt;
    }

    class GameView extends View {
        final static int BLANK = 0;
        final static int PLAY = 1;
        final static int DELAY = 1500;
        int status;
        ArrayList<Shape> arShape = new ArrayList<Shape>();
        Random Rnd = new Random();
        Activity mParent;

        public GameView(Context context){
            super(context);
            mParent = (Activity)context;
            status = BLANK;
            mHandler.sendEmptyMessageDelayed(0, DELAY);
        }
        public void onDraw(Canvas canvas){
            canvas.drawColor(Color.BLACK);
            if(status == BLANK){
                return;
            }

            int idx;
            for(idx = 0; idx < arShape.size(); idx++){
                Paint Pnt = new Paint();
                Pnt.setAntiAlias(true);
                Pnt.setColor(arShape.get(idx).color);

                Rect rt = arShape.get(idx).rt;
                switch(arShape.get(idx).what){
                    case Shape.RECT:
                        canvas.drawRect(rt, Pnt);
                        break;
                    case Shape.CIRCLE:
                        canvas.drawCircle(rt.left + rt.width()/2, rt.top + rt.height()/2, rt.width()/2, Pnt);
                        break;
                    case Shape.TRIANGLE:
                        Path path = new Path();
                        path.moveTo(rt.left + rt.width()/2, rt.top);
                        path.lineTo(rt.left, rt.bottom);
                        path.lineTo(rt.right, rt.bottom);
                        canvas.drawPath(path, Pnt);
                        break;
                }
            }
        }

        public boolean onTouchEvent(MotionEvent event){
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                int sel;
                sel = FindShapeIdx((int)event.getX(), (int)event.getY());
                if(sel == -1){
                    return true;
                }
                if(sel == arShape.size()-1){
                    status = BLANK;
                    invalidate();
                    mHandler.sendEmptyMessageDelayed(0, DELAY);
                }else{
                    new AlertDialog.Builder(getContext())
                            .setMessage("재미있지! 또 할래?")
                            .setTitle("게임 끝")
                            .setPositiveButton("함더", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int whichButton){
                                    arShape.clear();
                                    status = BLANK;
                                    invalidate();
                                    mHandler.sendEmptyMessageDelayed(0, DELAY);
                                }
                            })
                            .setNegativeButton("안해", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int whichButton){
                                    mParent.finish();
                                }
                            })
                            .show();
                }
                return true;
            }
            return false;
        }
        void AddNewShape(){
            Shape shape = new Shape();
            int idx;
            boolean bFindIntersect;
            Rect rt = new Rect();
            for(;;){
                int Size = 32+16*Rnd.nextInt(3);

                rt.left = Rnd.nextInt(getWidth());
                rt.top = Rnd.nextInt(getHeight());
                rt.right = rt.left + Size;
                rt.bottom = rt.top + Size;

                if(rt.right > getWidth() || rt.bottom > getHeight()){
                    continue;
                }
                bFindIntersect = false;
                for(idx = 0; idx < arShape.size(); idx++){
                    if(rt.intersect(arShape.get(idx).rt) == true){
                        bFindIntersect = true;
                    }
                }

                if(bFindIntersect == false){
                    break;
                }
            }

            shape.what = Rnd.nextInt(3);

            switch(Rnd.nextInt(5)){
                case 0:
                    shape.color = Color.WHITE;
                    break;
                case 1:
                    shape.color = Color.RED;
                    break;
                case 2:
                    shape.color = Color.GREEN;
                    break;
                case 3:
                    shape.color = Color.BLUE;
                    break;
                case 4:
                    shape.color = Color.YELLOW;
                    break;
            }
            shape.rt = rt;
            arShape.add(shape);
        }
        int FindShapeIdx(int x, int y){
            int idx;

            for(idx = 0; idx < arShape.size(); idx++){
                if(arShape.get(idx).rt.contains(x, y)){
                    return idx;
                }
            }
            return -1;
        }

        Handler mHandler = new Handler(){
            public void handleMessage(Message msg){
                AddNewShape();
                status = PLAY;
                invalidate();

                String title;
                title = "MemoryPower - " + arShape.size() + " 단계";
                mParent.setTitle(title);
            }
        };
    }
}
