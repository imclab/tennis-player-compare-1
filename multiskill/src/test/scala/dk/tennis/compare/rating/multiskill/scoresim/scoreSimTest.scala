package dk.tennis.compare.rating.multiskill.scoresim

import org.junit._
import Assert._
import dk.tennis.compare.rating.multiskill.matchloader.MatchesLoader
import dk.tennis.compare.rating.multiskill.model.perfdiff.Score
import dk.tennis.compare.rating.multiskill.model.perfdiff.Player
import dk.tennis.compare.rating.multiskill.infer.matchprob.MatchPrediction
import scala.math._
import breeze.plot.Figure
import breeze.plot.Plot
import breeze.plot._
import dk.bayes.math.gaussian.MultivariateGaussian
import dk.bayes.math.linear.Matrix
import java.util.Date
import dk.tennis.compare.rating.multiskill.model.perfdiff.skillsfactor.cov.skillovertime.SkillOverTimeCovFunc

class scoreSimTest {

  val matchesFile = "./src/test/resources/atp_historical_data/match_data_2006_2013.csv"
  val matchResults = MatchesLoader.loadMatches(matchesFile, 2010, 2011)

  val scores: Array[Score] = Score.toScores(matchResults)

  @Test def test {

    val meanFunc = (player: Player) => { if (player.onServe) 5d else 0 }
    val covFunc = SkillOverTimeCovFunc(Array(log(0.2), log(10), log(1), log(300)))
    val simulScores = scoreSim(scores, meanFunc, covFunc, logPerfStdDev = 2.3,randSeed=124066)

    val p1 = "Roger Federer"
    val p2 = "Novak Djokovic"
    val matchPredictions = MatchPrediction.toMatchPredictions(simulScores.map(s => s.score), simulScores.map(s => s.gamePerfDiff), matchResults.toIndexedSeq)

    matchPredictions.foreach { matchPrediction =>

      if (matchPrediction.hasPlayer(p1) && matchPrediction.hasPlayer(p2)) {
        println("%s:%.2f:%s".format(matchPrediction.matchTime, matchPrediction.matchProb(p1), matchPrediction.matchWinner()))
      }
      //   

    }

  }
}