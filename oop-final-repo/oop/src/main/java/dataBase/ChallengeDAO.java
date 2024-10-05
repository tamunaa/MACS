package dataBase;

import objects.Challenge;

import java.util.List;

public interface ChallengeDAO {
    public int addChallenge(Challenge challenge);
    public boolean removeChallenge(int id);
    public List<Challenge> getChallengeByChallengerID(int challengerID);
    public List<Challenge> getChallengeByChallengedID(int challengedID);
    public List<Challenge> getChallengeByQuizID(int quizID);
    public Challenge getChallengeByID(int id);
}
