package com.example.gameoflife

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.gameoflife.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var virtualGrid: GameBoard
    private lateinit var buttonGrid: Vector<Vector<Button>>

    private lateinit var cycleUpdateBoard:Runnable

    lateinit var  mainHandler: Handler

    private val width = 15
    private val height = 15

    private var playingTimeout:Long = 1000



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        virtualGrid = GameBoard(width, height)
        generateBoardView()

        mainHandler = Handler(Looper.getMainLooper())

        setUpControls()

        cycleUpdateBoard = Runnable {
            virtualGrid.progressBoard()
            refreshBoard()
            mainHandler.postDelayed(cycleUpdateBoard, playingTimeout)
        }


    }

    private fun setUpControls() {

        findViewById<ToggleButton>(R.id.playButton).setOnCheckedChangeListener{
            _, isChecked ->
            if (isChecked) {
                mainHandler.postDelayed(cycleUpdateBoard, playingTimeout)
            }else {
                mainHandler.removeCallbacks(cycleUpdateBoard)
            }
        }

        val speedSlider = findViewById<SeekBar>(R.id.speed);
        speedSlider.progress = 50
        speedSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                playingTimeout = 10000/(p1.toLong() + 1)
                println("seek progress ${p1.toLong()}, $playingTimeout")
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if(findViewById<ToggleButton>(R.id.playButton).isChecked){
                    mainHandler.removeCallbacks(cycleUpdateBoard)
                    mainHandler.postDelayed(cycleUpdateBoard, playingTimeout)
                }
            }

        })

        findViewById<Button>(R.id.up).setOnClickListener {
            virtualGrid.moveUp()
            refreshBoard()
        }

        findViewById<Button>(R.id.down).setOnClickListener{
            virtualGrid.moveDown()
            refreshBoard()
        }

        findViewById<Button>(R.id.left).setOnClickListener{
            virtualGrid.moveLeft()
            refreshBoard()
        }

        findViewById<Button>(R.id.right).setOnClickListener{
            virtualGrid.moveRight()
            refreshBoard()
        }


    }

    private fun clearBoard() {
        this.virtualGrid.clearState()
        refreshBoard()
    }


    private fun refreshBoard() {
        for (x in 0..width) {
            for (y in 0..height) {
                val button = this.buttonGrid[x][y]
                if(virtualGrid.getState(x,y)) {
                    button.setBackgroundColor(Color.YELLOW)
                }else {
                    button.setBackgroundColor(Color.BLACK)
                }
            }
        }
    }

    private fun generateBoardView() {
        val gameGrid = findViewById<TableLayout>(R.id.GameGrid);
        buttonGrid = Vector()
        for (x in 0..width) {
            val row = TableRow(this)
            val buttonRow = Vector<Button>()
            for (y in 0..height) {
                val myButton = Button(this)
                myButton.setOnClickListener { view ->
                    if (virtualGrid.getState(x, y)) {
                        view.setBackgroundColor(Color.BLACK)
                    } else {
                        view.setBackgroundColor(Color.YELLOW)
                    }
                    virtualGrid.toggleState(x,y)
                }
                myButton.setBackgroundColor(Color.BLACK)
                myButton.layoutParams = TableRow.LayoutParams(100, 100)
                row.addView(myButton)
                buttonRow.add(myButton)
            }
            buttonGrid.add(buttonRow)
            gameGrid.addView(row)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun resetBoard(): Boolean {
        clearBoard()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.reset_settings -> resetBoard()
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}


