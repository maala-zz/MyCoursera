package org.lenskit.mooc.nonpers.mean;

import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import org.lenskit.baseline.MeanDamping;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.ratings.Rating;
import org.lenskit.inject.Transient;
import org.lenskit.util.io.ObjectStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Provider class that builds the mean rating item scorer, computing damped item means from the
 * ratings in the DAO.
 */
public class DampedItemMeanModelProvider implements Provider<ItemMeanModel> {
    /**
     * A logger that you can use to emit debug messages.
     */
    private static final Logger logger = LoggerFactory.getLogger(DampedItemMeanModelProvider.class);

    /**
     * The data access object, to be used when computing the mean ratings.
     */
    private final DataAccessObject dao;
    /**
     * The damping factor.
     */
    private final double damping;
    private double damping1;

    /**
     * Constructor for the mean item score provider.
     *
     * <p>The {@code @Inject} annotation tells LensKit to use this constructor.
     *
     * @param dao The data access object (DAO), where the builder will get ratings.  The {@code @Transient}
     *            annotation on this parameter means that the DAO will be used to build the model, but the
     *            model will <strong>not</strong> retain a reference to the DAO.  This is standard procedure
     *            for LensKit models.
     * @param damping The damping factor for Bayesian damping.  This is number of fake global-mean ratings to
     *                assume.  It is provided as a parameter so that it can be reconfigured.  See the file
     *                {@code damped-mean.groovy} for how it is used.
     */
    @Inject
    public DampedItemMeanModelProvider(@Transient DataAccessObject dao,
                                       @MeanDamping double damping) {
        this.dao = dao;
        this.damping = damping;
    }

    /**
     * Construct an item mean model.
     *
     * <p>The {@link Provider#get()} method constructs whatever object the provider class is intended to build.</p>
     *
     * @return The item mean model with mean ratings for all items.
     */
    @Override
    public ItemMeanModel get() {
        Map<Long,Double> movie_rating = new HashMap<>();
        Map<Long,Double> movie_raters = new HashMap<>();
        double totalRating = 0.0 , totalRate = 0.0;
        try (ObjectStream<Rating> ratings = dao.query(Rating.class).stream()) {
            for (Rating r: ratings) {
                totalRating+=r.getValue();
                totalRate+=1.0 ;
                if( !movie_rating.containsKey(r.getItemId()) ){
                    movie_rating.put(r.getItemId(),0.0) ;
                }
                if( !movie_raters.containsKey(r.getItemId()) ){
                    movie_raters.put(r.getItemId(),0.0) ;
                }
                movie_rating.put(r.getItemId(),movie_rating.get(r.getItemId())+r.getValue()) ;
                movie_raters.put(r.getItemId(),movie_raters.get(r.getItemId())+1.0) ;
            }
        }
        double global_mean = totalRating / totalRate ;
        Long2DoubleOpenHashMap means = new Long2DoubleOpenHashMap();
        for (Map.Entry<Long, Double> entry : movie_rating.entrySet()) {
            double enm = movie_rating.get(entry.getKey()) + this.damping*global_mean ;
            double den = movie_raters.get(entry.getKey()) + damping ;
            means.put(entry.getKey(), new Double(enm/den ) );
        }
        logger.info("computed mean ratings for {} items", means.size());
        return new ItemMeanModel(means);
    }
}
