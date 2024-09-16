package com.example.testgame

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var gameStatus: TextView
    private lateinit var attackButton: Button
    private lateinit var defendButton: Button
    private lateinit var healButton: Button
    private lateinit var heroImage: ImageView
    private lateinit var enemyImage: ImageView

    private lateinit var hero: Hero
    private lateinit var enemy: Enemy
    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameStatus = findViewById(R.id.gameStatus)
        attackButton = findViewById(R.id.attackButton)
        defendButton = findViewById(R.id.defendButton)
        healButton = findViewById(R.id.healButton)
        heroImage = findViewById(R.id.heroImage)
        enemyImage = findViewById(R.id.enemyImage)

        hero = Hero("Goku", 200, 132, 76)
        enemy = Enemy("Naruto", 200, 89, 51)
        game = Game(hero, enemy)

        updateGameStatus()

        attackButton.setOnClickListener {
            heroTurn(ActionType.ATTACK)
        }

        defendButton.setOnClickListener {
            heroTurn(ActionType.DEFEND)
        }

        healButton.setOnClickListener {
            heroTurn(ActionType.HEAL)
        }
    }

    private fun heroTurn(actionType: ActionType) {
        when (actionType) {
            ActionType.ATTACK -> {
                hero.attack(enemy)
                performAttackAnimation(heroImage, enemyImage)
            }
            ActionType.DEFEND -> {
                hero.defend()
                performDefendAnimation(heroImage)
            }
            ActionType.HEAL -> {
                hero.heal()
                performHealAnimation(heroImage)
            }
        }

        if (!enemy.isAlive()) {
            gameOver(hero)
            return
        }

        enemyTurn()

        if (!hero.isAlive()) {
            gameOver(enemy)
            return
        }

        updateGameStatus()
    }

    private fun enemyTurn() {
        when ((1..3).random()) {
            1 -> {
                enemy.attack(hero)
                performAttackAnimation(enemyImage, heroImage)
            }
            2 -> enemy.defend()
            3 -> {
                enemy.heal()
                performHealAnimation(enemyImage)
            }
        }
    }

    private fun performAttackAnimation(attacker: ImageView, target: ImageView) {
        attacker.animate()
            .translationXBy(-20f)
            .translationXBy(20f)
            .setDuration(100)
            .withEndAction {
                attacker.translationX = 0f
            }

        target.animate()
            .alpha(0.5f)
            .setDuration(100)
            .withEndAction {
                target.alpha = 1f
            }
    }

    private fun performHealAnimation(character: ImageView) {
        ObjectAnimator.ofFloat(character, "scaleX", 1.2f).apply {
            duration = 300
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
            start()
        }

        ObjectAnimator.ofFloat(character, "scaleY", 1.2f).apply {
            duration = 300
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
            start()
        }
    }

    private fun performDefendAnimation(character: ImageView) {
        character.animate()
            .translationYBy(-30f)
            .setDuration(300)
            .withEndAction {
                character.translationY = 0f
            }
    }

    private fun gameOver(winner: Character) {
        gameStatus.text = "Game over! ${winner.name} wins!"
        disableButtons()
    }

    private fun updateGameStatus() {
        gameStatus.text = "${hero.name}: ${hero.hp} HP\n${enemy.name}: ${enemy.hp} HP"
    }

    private fun disableButtons() {
        attackButton.isEnabled = false
        defendButton.isEnabled = false
        healButton.isEnabled = false
    }
}

enum class ActionType {
    ATTACK, DEFEND, HEAL
}

open class Character(val name: String, var hp: Int, val attack: Int, val defense: Int) {
    open fun attack(target: Character) {
        val damage = (this.attack - target.defense).coerceAtLeast(0)
        target.hp -= damage
        println("${this.name} attacks ${target.name} for $damage damage!")
    }

    open fun defend() {
        println("${this.name} defends!")
    }

    open fun heal() {
        val healingAmount = (15..25).random()
        this.hp += healingAmount
        println("${this.name} heals for $healingAmount HP!")
    }

    fun isAlive(): Boolean {
        return this.hp > 0
    }
}

class Hero(name: String, hp: Int, attack: Int, defense: Int) : Character(name, hp, attack, defense)
class Enemy(name: String, hp: Int, attack: Int, defense: Int) : Character(name, hp, attack, defense)
class Game(val hero: Hero, val enemy: Enemy)