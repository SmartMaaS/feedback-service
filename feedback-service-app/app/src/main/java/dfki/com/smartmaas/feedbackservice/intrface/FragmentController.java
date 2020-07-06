package dfki.com.smartmaas.feedbackservice.intrface;


import dfki.com.smartmaas.feedbackservice.model.CustomFragment;

public interface FragmentController {
    void replaceFragment(CustomFragment fragment, String tag, boolean addToBackStack,
                         int anim_in, int anim_out);

    void addFragment(CustomFragment fragment, String tag, boolean addToBackStack,
                     int anim_in, int anim_out);

    void attachFragment(CustomFragment fragment, String tag, boolean addToBackStack,
                        int anim_in, int anim_out);

    void detachFragment(CustomFragment fragment, String tag, boolean addToBackStack,
                        int anim_in, int anim_out);

    void removeFragment(CustomFragment fragment);
}
