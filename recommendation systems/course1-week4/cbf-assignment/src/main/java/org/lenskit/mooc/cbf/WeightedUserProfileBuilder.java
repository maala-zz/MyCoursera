package org.lenskit.mooc.cbf;

import org.lenskit.data.ratings.Rating;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Build a user profile from all positive ratings.
 */
public class WeightedUserProfileBuilder implements UserProfileBuilder {
    /**
     * The tag model, to get item tag vectors.
     */
    private final TFIDFModel model;

    @Inject
    public WeightedUserProfileBuilder(TFIDFModel m) {
        model = m;
    }

    @Override
    public Map<String, Double> makeUserProfile(@Nonnull List<Rating> ratings) {
        // Create a new vector over tags to accumulate the user profile
        Map<String,Double> profile = new HashMap<>();
        double avg = 0.0;
        for (Rating r: ratings) {
            avg+=r.getValue();
        }
        avg/=new Double(ratings.size()) ;
        for (Rating r: ratings) {
            Map<String,Double> tv = model.getItemVector(r.getItemId());
            for (Map.Entry<String,Double> entry : tv.entrySet()) {
                if( profile.get(entry.getKey()) == null )
                    profile.put(entry.getKey(),0.0) ;
                profile.put(entry.getKey(), profile.get(entry.getKey()) + (r.getValue()-avg)*entry.getValue().doubleValue() );
            }
        }
        // TODO Normalize the user's ratings
        // TODO Build the user's weighted profile


        // The profile is accumulated, return it.
        return profile;
    }
}
