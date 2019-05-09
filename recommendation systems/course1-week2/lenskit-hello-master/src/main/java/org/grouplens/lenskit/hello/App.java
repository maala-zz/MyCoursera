package org.grouplens.lenskit.hello;

import javafx.util.Pair;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class App {
    private static final String COMMA_DELIMITER ="," ;
    Integer ratingSum = 0 , votersSum = 0, FemaleRatingSum = 0, MaleRatingSum = 0 ;
    Integer sumMaleVotes = 0,sumFemaleVotes = 0;
    public static List<String> movies = new ArrayList<>();
    public static Map<String,Integer> totalRatingValues = new HashMap<>() ;
    public static Map<String,Integer> totalVoters = new HashMap<>() ;
    public static Map<String,Integer> totalVotersPlus4 = new HashMap<>() ;
    public static Map<String,Integer> totalMaleVoters = new HashMap<>() ;
    public static Map<String,Integer> totalMaleVotersPlus4 = new HashMap<>() ;
    public static Map<String,Integer> totalMaleRating = new HashMap<>() ;
    public static Map<String,Integer> totalFemaleRating = new HashMap<>() ;
    public static Map<String,Integer> totalFemaleVoters = new HashMap<>() ;
    public static Map<String,Integer> totalFemaleVotersPlus4 = new HashMap<>() ;

    int user_movie[][] = new int [100][100] ;
    public void run(){
        int cnt = 0 ;
        List<String> records = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("data/HW1-data.csv"));) {
            while (scanner.hasNextLine()) {
                String s = scanner.nextLine() ;
                records = getRecordFromLine(s);
                if( records.get(0).equals("User") ){
                    for( int i = 2 ; i < records.size() ; ++i ) {
                        movies.add(records.get(i));
                        totalRatingValues.put(records.get(i), 0);
                        totalVoters.put(records.get(i),0);
                        totalFemaleVoters.put(records.get(i),0);
                        totalMaleVoters.put(records.get(i),0);
                        totalVotersPlus4.put(records.get(i),0) ;
                        totalMaleRating.put(records.get(i),0) ;
                        totalFemaleRating.put(records.get(i),0) ;
                        totalMaleVotersPlus4.put(records.get(i),0) ;
                        totalFemaleVotersPlus4.put(records.get(i),0) ;
                    }
                    System.out.println() ;
                }
                else
                for( int i = 2 ; i < records.size() ; ++i ){
                        if( records.get(i).trim().equals("") )continue;
                        String movie = movies.get(i-2) ;
                        int rating = new Integer(records.get(i).trim()) ;
                        user_movie[cnt][i-2] = rating ;
                        ratingSum+=rating;
                        votersSum++;
                        totalRatingValues.put(movie,totalRatingValues.get(movie).intValue()+rating) ;
                        totalVoters.put(movie,totalVoters.get(movie)+1) ;
                        if( rating >= 4 ) {
                            totalVotersPlus4.put(movie, totalVotersPlus4.get(movie).intValue() + 1);
                        }
                        if( records.get(1).trim().equals("0") ){
                            totalMaleVoters.put(movie,totalMaleVoters.get(movie).intValue()+1) ;
                            totalMaleRating.put(movie,totalMaleRating.get(movie).intValue()+rating) ;
                            MaleRatingSum+=rating ;
                            sumMaleVotes++ ;
                            if( rating >= 4 )
                                totalMaleVotersPlus4.put(movie,totalMaleVotersPlus4.get(movie).intValue()+rating ) ;
                        }
                        else{
                            totalFemaleVoters.put(movie,totalFemaleVoters.get(movie).intValue()+1) ;
                            totalFemaleRating.put(movie,totalFemaleRating.get(movie).intValue()+1) ;
                            FemaleRatingSum+=rating ;
                            sumFemaleVotes++;
                            if( rating >= 4 ){
                                totalFemaleVotersPlus4.put(movie,totalFemaleVotersPlus4.get(movie).intValue()+1 ) ;
                            }

                        }
                }
                cnt++ ;
          } // end while
//            calc_highest_mean_movies_rating() ;
//            calc_highest_movies_rating();
//            calc_percentage_movies_ratings();
//            calc_association_rule_ToyStore();
//              calc_correlation_ToyStory();
  //            calc_Female_Male_diff() ;
                calc_percentage_Female_Male() ;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void calc_highest_movies_rating(){
        List<Pair<Integer, String>> voting_movie = new ArrayList<>();
        for( int i = 0 ; i < movies.size() ; ++i ){
            String movie = movies.get(i) ;
            voting_movie.add(new Pair(totalVoters.get(movie),movie));
        }
        Collections.sort(voting_movie, new Comparator<Pair<Integer, String>>() {
            @Override
            public int compare(final Pair<Integer, String> o1, final Pair<Integer, String> o2) {
                if( o1.getKey() < o2.getKey() ) return -1 ;
                if( o1.getKey() == o2.getKey() )return 0 ;
                return 1 ;
            }
        });
        Collections.reverse(voting_movie);
        System.out.println("*****************************************") ;
        for( int i = 0 ; i < voting_movie.size() ; ++i ){
            System.out.print("movie with the highest number of voting is "+ voting_movie.get(i).getValue()) ;
            System.out.println(" with total voters equal to "+ voting_movie.get(i).getKey()) ;
        }
    }
    public void calc_highest_mean_movies_rating(){
        List<Pair<Double, String>> rating_movie = new ArrayList<>();
        for( int i = 0 ; i < movies.size() ; ++i ){
            String movie = movies.get(i) ;
            rating_movie.add(new Pair(new Double(totalRatingValues.get(movie)) /new Double(totalVoters.get(movie)),movie));
        }
        Collections.sort(rating_movie, new Comparator<Pair<Double, String>>() {
            @Override
            public int compare(final Pair<Double, String> o1, final Pair<Double, String> o2) {
                if(o1.getKey() < o2.getKey()) return -1 ;
                if( o1.getKey() == o2.getKey() )return 0 ;
                return 1 ;
            }
        });
        Collections.reverse( rating_movie ) ;
        for( int i = 0 ; i < rating_movie.size() ; ++i ){
            System.out.print("movie with the highest mean rating is "+ rating_movie.get(i).getValue()) ;
            System.out.println(" with mean rating equal to "+ rating_movie.get(i).getKey()) ;
        }
    }
    public void calc_percentage_movies_ratings(){
        List<Pair<Double, String>> voting_movie = new ArrayList<>();
        for( int i = 0 ; i < movies.size() ; ++i ){
            String movie = movies.get(i) ;
            voting_movie.add(new Pair(new Double(totalVotersPlus4.get(movie))/new Double(totalVoters.get(movie)),movie));
        }
        Collections.sort(voting_movie, new Comparator<Pair<Double, String>>() {
            @Override
            public int compare(final Pair<Double, String> o1, final Pair<Double, String> o2) {
                if( o1.getKey() < o2.getKey() ) return -1 ;
                if( o1.getKey() == o2.getKey() )return 0 ;
                return 1 ;
            }
        });
        Collections.reverse(voting_movie);
        System.out.println("*****************************************") ;
        for( int i = 0 ; i < voting_movie.size() ; ++i ){
            System.out.print("movie with the highest percentage of ratings  is "+ voting_movie.get(i).getValue()) ;
            System.out.println(" with percentage equal to "+ new Double(voting_movie.get(i).getKey())) ;
        }
    }
    public void calc_association_rule_ToyStore() {
        List<Pair<Double, Integer>> assoc_movie = new ArrayList<>();
        int M1 = 0 ;
        for( int i = 0 ; i < 100 ; ++i )
            M1+=user_movie[i][6] ;
        for (int m = 0; m < 100; ++m) {
       //     if( m == 6 )continue; ;
            int cnt = 0 ;
            for (int u = 0; u < 100; ++u) {
                if(user_movie[u][m] != 0 && user_movie[u][6] != 0)
                cnt++ ;
            }
            assoc_movie.add(new Pair( new Double(cnt) / new Double(M1) , m ));
            Collections.sort(assoc_movie, new Comparator<Pair<Double, Integer>>() {
                @Override
                public int compare(final Pair<Double, Integer> o1, final Pair<Double, Integer> o2) {
                    if( o1.getKey() < o2.getKey() ) return -1 ;
                    if( o1.getKey() == o2.getKey() )return 0 ;
                    return 1 ;
                }
            });
            Collections.reverse(assoc_movie);
        }
        for( int i = 0 ; i < assoc_movie.size() ; ++i ){
            System.out.println("the movie "+movies.get(assoc_movie.get(i).getValue())+" has association with movie Toy Store equal "+ assoc_movie.get(i).getKey()) ;
        }
    }
    public void calc_Female_Male_diff(){
        List<Pair<Double,Integer>> diffFemaleMale = new ArrayList<>();
        List<Pair<Double,Integer>> diffMaleFemale = new ArrayList<>();
        double overallDiff = 0.0 ;
        for(int i = 0 ; i < movies.size(); ++i){
            String movie = movies.get(i) ;
            double FemaleAvg = new Double( totalFemaleRating.get(movie) ) / new Double(totalFemaleVoters.get(movie)) ;
            double MaleAvg = new Double( totalMaleRating.get(movie) ) / new Double(totalMaleVoters.get(movie)) ;
            diffFemaleMale.add(new Pair( FemaleAvg - MaleAvg , i ));
            diffMaleFemale.add(new Pair( MaleAvg - FemaleAvg , i ));
            overallDiff+=FemaleAvg-MaleAvg ;
        }
        overallDiff/=new Double(movies.size()) ;
        Collections.sort(diffFemaleMale, new Comparator<Pair<Double,Integer>>() {
            @Override
            public int compare(final Pair<Double,Integer> o1, final Pair<Double,Integer> o2) {
                if( o1.getKey() < o2.getKey() ) return -1 ;
                if( o1.getKey() == o2.getKey() )return 0 ;
                return 1 ;
            }
        });
        Collections.sort(diffMaleFemale, new Comparator<Pair<Double,Integer>>() {
            @Override
            public int compare(final Pair<Double,Integer> o1, final Pair<Double,Integer> o2) {
                if( o1.getKey() < o2.getKey() ) return -1 ;
                if( o1.getKey() == o2.getKey() )return 0 ;
                return 1 ;
            }
        });
        Collections.reverse(diffFemaleMale);
        Collections.reverse(diffMaleFemale);
        for( int i = 0 ; i < diffFemaleMale.size() ; ++i ){
            System.out.println("movie with the largest difference between female and male is "+
                                movies.get(diffFemaleMale.get(i).getValue())+" with difference equal to "+diffFemaleMale.get(i).getKey()) ;
        }
        System.out.println("************************************************************");
        for( int i = 0 ; i < diffMaleFemale.size() ; ++i ){
            System.out.println("movie with the largest difference between male and female is "+
                    movies.get(diffMaleFemale.get(i).getValue())+" with difference equal to "+diffMaleFemale.get(i).getKey()) ;
        }
        System.out.println("over all average difference "+(new Double(FemaleRatingSum)/new Double(sumFemaleVotes)-new Double(MaleRatingSum)/new Double(sumMaleVotes))) ;
    }
    public void calc_percentage_Female_Male(){
        List<Pair<Double,Integer>> diffFemaleMale = new ArrayList<>();
        List<Pair<Double,Integer>> diffMaleFemale = new ArrayList<>();
        for(int i = 0 ; i < movies.size(); ++i){
            String movie = movies.get(i) ;
            double FemalePer = new Double( totalFemaleVotersPlus4.get(movie) ) / new Double(totalFemaleVoters.get(movie)) ;
            double MalePer = new Double( totalMaleVotersPlus4.get(movie) ) / new Double(totalMaleVoters.get(movie)) ;
            diffFemaleMale.add(new Pair( FemalePer - MalePer , i ));
            diffMaleFemale.add(new Pair( MalePer - FemalePer , i ));
        }
        Collections.sort(diffFemaleMale, new Comparator<Pair<Double,Integer>>() {
            @Override
            public int compare(final Pair<Double,Integer> o1, final Pair<Double,Integer> o2) {
                if( o1.getKey() < o2.getKey() ) return -1 ;
                if( o1.getKey() == o2.getKey() )return 0 ;
                return 1 ;
            }
        });
        Collections.sort(diffMaleFemale, new Comparator<Pair<Double,Integer>>() {
            @Override
            public int compare(final Pair<Double,Integer> o1, final Pair<Double,Integer> o2) {
                if( o1.getKey() < o2.getKey() ) return -1 ;
                if( o1.getKey() == o2.getKey() )return 0 ;
                return 1 ;
            }
        });
        Collections.reverse(diffFemaleMale);
        Collections.reverse(diffMaleFemale);
        for( int i = 0 ; i < diffFemaleMale.size() ; ++i ){
            System.out.println("movie with the largest difference between female and male is "+
                    movies.get(diffFemaleMale.get(i).getValue())+" with difference equal to "+diffFemaleMale.get(i).getKey()) ;
        }
        System.out.println("************************************************************");
        for( int i = 0 ; i < diffMaleFemale.size() ; ++i ){
            System.out.println("movie with the largest difference between male and female is "+
                    movies.get(diffMaleFemale.get(i).getValue())+" with difference equal to "+diffMaleFemale.get(i).getKey()) ;
        }
    }
    public String getMovieId(String movie){
        String movieId = "" ;
        for( int i = 0 ; i < movie.length() ; ++i ){
            if( movie.charAt(i) == '\"' )continue;
            if( movie.charAt(i) == ':' )break;
            movieId+=movie.charAt(i) ;
        }
        return movieId ;
    }
    private List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(COMMA_DELIMITER);
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }
}
