
import java.util.Date;

public class MeaningOfLife {
  static boolean DEBUG = false;
  public static String findOutWhatLifeIsAllAbout() {
    int meaning = 0;
    System.out.println( new Date() );
    for ( int i = 0; i < 10; i++ ) {
      if ( DEBUG )
        System.out.println( "LOOP 1: INTERATION " + i );
      for ( int j = 0; j < 20; j++ ) {
        if ( DEBUG )
          System.out.println( "LOOP 2: INTERATION " + j );
        for ( int k = 0; k < 300; k++ ) {
          if ( DEBUG && k % 30 == 0 )
            System.out.println( "LOOP 3: INTERATION " + k );
          for ( int m = 0; m < 7000; m++ ) {
            if ( DEBUG && m % 1000 == 0 )
              System.out.println( "LOOP 4: INTERATION " + m );
            meaning += Math.random() + 1;
          }
        }
      }
    }
    System.out.println( new Date() );
    return String.valueOf( meaning ).replaceAll( "0*$", "" );
  }

  public static void main( String[] args ) {
    System.out.println( findOutWhatLifeIsAllAbout() );
  }
}

    
