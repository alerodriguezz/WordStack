/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.wordstack;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final int WORD_LENGTH = 5;
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private ViewGroup word1_box;
    private ViewGroup word2_box;
    private ArrayList<String> words = new ArrayList<>();
    private Random random = new Random();
    private StackedLayout stackedLayout;
    private String word1, word2;
    private Stack<LetterTile> placedTiles = new Stack();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = in.readLine()) != null) {
                String word = line.trim();
                //add words from dictionary to words
                if (word.length() == WORD_LENGTH) {
                    words.add(word);
                }
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
        stackedLayout = new StackedLayout(this);
        verticalLayout.addView(stackedLayout, 3);

        word1_box = (ViewGroup) findViewById(R.id.word1);
        word1_box.setOnTouchListener(new TouchListener());
        //word1LinearLayout.setOnDragListener(new DragListener());
        word2_box = (ViewGroup) findViewById(R.id.word2);
        word2_box.setOnTouchListener(new TouchListener());
        //word2LinearLayout.setOnDragListener(new DragListener());
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !stackedLayout.empty()) {
                LetterTile tile = (LetterTile) stackedLayout.peek();
                tile.moveToViewGroup((ViewGroup) v);
                if (stackedLayout.empty()) {
                    TextView messageBox = (TextView) findViewById(R.id.message_box);
                    messageBox.setText(word1 + " " + word2);
                }
                placedTiles.push(tile);
                return true;
            }
            return false;
        }
    }

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    LetterTile tile = (LetterTile) event.getLocalState();
                    tile.moveToViewGroup((ViewGroup) v);
                    if (stackedLayout.empty()) {
                        TextView messageBox = (TextView) findViewById(R.id.message_box);
                        messageBox.setText(word1 + " " + word2);
                    }
                    /**
                     **
                     **  YOUR CODE GOES HERE
                     **
                     **/
                    return true;
            }
            return false;
        }
    }

    public boolean onStartGame(View view) {
        //removes any pre-existing tiles on
        word1_box.removeAllViews();
        word2_box.removeAllViews();
        stackedLayout.clear();


        Log.i("size","Starting Game.... ");
        TextView messageBox = (TextView) findViewById(R.id.message_box);
        messageBox.setText("Game started");

        Log.i("size","Testing " + words.size());
        //counters
        int count_w1 =0,count_w2=0;

        //random variable

        int n = random.nextInt(words.size());

        //Retrieve two random words from words and remove  them to avoid duplicates
        word1=words.get(n);

        //words.remove(n);

        n = random.nextInt(words.size());

        word2=words.get(n);

        Log.i("Random Words","word1: " + word1 + " word2: " + word2);

        //words.remove(n);

        //Scramble the word

        String temp="";

        while(count_w1!=word1.length()&&count_w2!=word2.length())
        {
            n = random.nextInt(2);


            if(n==0)
            {
                temp +=word1.charAt(count_w1);
                count_w1++;
            }

            else
            {
                temp+= word2.charAt(count_w2);
                count_w2++;
            }

            Log.i("switch", "rand: " + n + " count 1: " +count_w1+ " count 2: "+ count_w2+" " + temp);
        }

        //if word 1 ended first
        if(count_w1==word1.length())
        {
            //add remaining characters of word2 to temp
            for(int i=count_w2;i<word2.length();i++)
            {
                temp+= word2.charAt(i);
            }
        }
        //vice versa
        else
        {
            //add remaining characters of word2 to temp
            for(int i=count_w1;i<word1.length();i++)
            {
                temp+= word1.charAt(i);
            }
        }

        Log.i("scrambled word", "scrambled word: " + temp);

        messageBox.setText(temp);

        //add elements to arraylist
        ArrayList<LetterTile> t = new ArrayList<LetterTile>();

        for(int i=temp.length()-1;i>0;i--){
            stackedLayout.push(new LetterTile(this, temp.charAt(i)));
        }

        Log.i("scrambled word", "...Done Stacking");
        return true;
    }

    public boolean onUndo(View view) {
        Log.i("scrambled word", "Undoing....");
        if(placedTiles.empty())
        {
            return false;
        }
        LetterTile tile = placedTiles.pop();
        tile.moveToViewGroup(stackedLayout);

        return true;
    }
}
