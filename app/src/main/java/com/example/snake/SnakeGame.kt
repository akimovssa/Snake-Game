import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snake.R
import com.example.snake.ui.theme.pixelifySansFamily
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Cell(val x: Int, val y: Int)

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

@Composable
fun SnakeGame() {
    val gridSize = 20

    var direction by remember { mutableStateOf(Direction.RIGHT) }
    var snake by remember { mutableStateOf(listOf(Cell(5, 5))) }
    var food by remember {
        mutableStateOf(
            generateFood(
                snake,
                gridSize
            )
        )
    }
    var isGameOver by remember { mutableStateOf(false) }
    var gameId by remember { mutableStateOf(0) }
    var gameSpeed by remember { mutableStateOf(50L) }
    var snakeSpeed by remember { mutableStateOf(200L) }
    var lastMoveTime by remember { mutableStateOf(0L) }

    LaunchedEffect(gameId) {
        while (!isGameOver) {
            delay(gameSpeed)

            if (System.currentTimeMillis() - lastMoveTime >= snakeSpeed) {
                snake = moveSnake(snake, direction)
                lastMoveTime = System.currentTimeMillis()
            }
            if (snake.first() == food) {
                food = generateFood(snake, gridSize)
                snake = growSnake(snake, direction, gridSize)
            }
            isGameOver = checkGameOver(snake, gridSize)
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (isGameOver) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Счёт: ${snake.size - 1}",
                    color = colorResource(R.color.whitespace),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = pixelifySansFamily,
                    modifier = Modifier
                        .padding(50.dp)
                )

                OutlinedButton(
                    onClick = {
                        snake = listOf(Cell(5, 5))
                        direction = Direction.RIGHT
                        food = generateFood(snake, gridSize)
                        isGameOver = false
                        gameId++
                    },
                    modifier = Modifier
                        .fillMaxWidth(.35f)
                        .height(50.dp),
                    border = BorderStroke(2.dp, Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Играть",
                        color = colorResource(R.color.whitespace),
                        maxLines = 1,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = pixelifySansFamily
                    )
                }
            }
        } else {
            GameBoard(snake, food, gridSize, direction, { direction = it })
        }
    }
}

@Composable
fun GameScore(score: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp, 50.dp)
    ) {
        Text(
            text = "Счёт: $score",
            color = colorResource(R.color.whitespace),
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = pixelifySansFamily
        )
    }
}

@Composable
fun GameBoard(
    snake: List<Cell>,
    food: Cell,
    gridSize: Int,
    currentDirection: Direction,
    onDirectionChange: (Direction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.blackspace)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GameScore(snake.size - 1)
        Spacer(modifier = Modifier.weight(.1f))
        Grid(snake, food, gridSize)
        Spacer(modifier = Modifier.weight(.1f))
        Controls(currentDirection, onDirectionChange)
        Spacer(modifier = Modifier.weight(.1f))
    }
}

@Composable
fun Grid(snake: List<Cell>, food: Cell, gridSize: Int) {
    val cellSize = 16.dp

    Column(modifier = Modifier
        .background(color = Color.Red)
    ) {
        for (i in 0 until gridSize) {
            Row {
                for (j in 0 until gridSize) {
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .border(border = BorderStroke(.2.dp, colorResource(R.color.blackspace_2)))
                            .background(
                                when (Cell(j, i)) {
                                    in snake -> Color.Green
                                    food -> Color.Red
                                    else -> Color.Black
                                }
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun Controls(currentDirection: Direction, onDirectionChange: (Direction) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            OutlinedButton(
                onClick = { if (currentDirection != Direction.DOWN) onDirectionChange(Direction.UP) },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, colorResource(R.color.whitesmoke)),
                modifier = Modifier
                    .size(65.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    tint = colorResource(R.color.whitesmoke),
                    contentDescription = "Up"
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(8.dp)
        ) {
            OutlinedButton(
                onClick = { if (currentDirection != Direction.RIGHT) onDirectionChange(Direction.LEFT) },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, colorResource(R.color.whitesmoke)),
                modifier = Modifier
                    .size(65.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    tint = colorResource(R.color.whitesmoke),
                    contentDescription = "Left"
                )
            }

            Spacer(modifier = Modifier.width(80.dp))

            OutlinedButton(
                onClick = { if (currentDirection != Direction.LEFT) onDirectionChange(Direction.RIGHT) },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, colorResource(R.color.whitesmoke)),
                modifier = Modifier
                    .size(65.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    tint = colorResource(R.color.whitesmoke),
                    contentDescription = "Right"
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            OutlinedButton(
                onClick = { if (currentDirection != Direction.UP) onDirectionChange(Direction.DOWN) },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, colorResource(R.color.whitesmoke)),
                modifier = Modifier
                    .size(65.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    tint = colorResource(R.color.whitesmoke),
                    contentDescription = "Down"
                )
            }
        }
    }
}

fun moveSnake(snake: List<Cell>, direction: Direction): List<Cell> {
    val head = snake.first()
    val newHead = when (direction) {
        Direction.UP -> Cell(head.x, head.y - 1)
        Direction.DOWN -> Cell(head.x, head.y + 1)
        Direction.LEFT -> Cell(head.x - 1, head.y)
        Direction.RIGHT -> Cell(head.x + 1, head.y)
    }
    val newSnake = snake.toMutableList()
    newSnake.add(0, newHead)
    newSnake.removeAt(newSnake.size - 1)
    return newSnake
}

fun generateFood(snake: List<Cell>, gridSize: Int): Cell {
    val emptyCells = (0 until gridSize).flatMap { x ->
        (0 until gridSize).map { y -> Cell(x, y) }
    }.filter { it !in snake }
    return emptyCells[Random.nextInt(emptyCells.size)]
}

fun growSnake(snake: List<Cell>, direction: Direction, gridSize: Int): List<Cell> {
    val growth = when (direction) {
        Direction.UP -> Cell(snake.first().x, (snake.first().y - 1 + gridSize) % gridSize)
        Direction.DOWN -> Cell(snake.first().x, (snake.first().y + 1) % gridSize)
        Direction.LEFT -> Cell((snake.first().x - 1 + gridSize) % gridSize, snake.first().y)
        Direction.RIGHT -> Cell((snake.first().x + 1) % gridSize, snake.first().y)
    }
    return listOf(growth) + snake
}

fun checkGameOver(snake: List<Cell>, gridSize: Int): Boolean {
    val head = snake.first()
    return head in snake.drop(1) || head.x < 0 || head.y < 0 || head.x >= gridSize || head.y >= gridSize
}

@Preview
@Composable
fun SnakeGamePreview() {
    SnakeGame()
}