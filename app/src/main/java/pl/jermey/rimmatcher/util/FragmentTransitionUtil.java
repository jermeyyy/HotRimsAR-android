package pl.jermey.rimmatcher.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Created by Jermey on 30.11.2016.
 */

public class FragmentTransitionUtil implements FragmentManager.OnBackStackChangedListener {
    private static final String TAG = "FragmentTransitionUtil";
    private static WeakHashMap<FragmentManager, FragmentTransitionUtil> instances =
            new WeakHashMap<>();
    private static final String INTERMEDIATE_PUSH = "intermediatePush";
    private static final String INTERMEDIATE_POP = "intermediatePop";

    private static final SparseArray<FragmentTransitionInfo> tags = new SparseArray<>();
    private final WeakReference<FragmentManager> fragmentManagerRef;

    public static FragmentTransitionUtil getInstance(FragmentManager fragmentManager) {
        synchronized (instances) {
            FragmentTransitionUtil instance = instances.get(fragmentManager);
            if (instance == null) {
                instance = new FragmentTransitionUtil(fragmentManager);
                fragmentManager.addOnBackStackChangedListener(instance);
                instances.put(fragmentManager, instance);
            }
            return instance;
        }
    }

    private FragmentTransitionUtil(FragmentManager fragmentManager) {
        this.fragmentManagerRef = new WeakReference<>(fragmentManager);
    }

    public void transition(int container, Fragment from, Fragment to, View sharedElement,
                           String sharedElementName) {
        final FragmentManager fragmentManager = fragmentManagerRef.get();
        if (fragmentManager == null) {
            return;
        }
        final int depth = fragmentManager.getBackStackEntryCount();
        final String tag = UUID.randomUUID().toString();
        tags.put(depth, new FragmentTransitionInfo(from.getTag(), tag, sharedElement,
                sharedElementName));
        fragmentManager
                .beginTransaction()
                .add(container, to, tag)
                .hide(to)
                .addToBackStack(INTERMEDIATE_PUSH)
                .commit();
    }

    /**
     * This is needed to work around a bug in fragment transitions.
     * See MainActivityFragment.onHiddenChanged for how to use this.
     */
    public String getTransitionName() {
        final FragmentManager fragmentManager = fragmentManagerRef.get();
        if (fragmentManager == null) {
            return null;
        }
        final int depth = fragmentManager.getBackStackEntryCount();
        FragmentTransitionInfo transitionInfo = tags.get(depth);
        if (transitionInfo == null && depth >= 1) {
            String name = fragmentManager.getBackStackEntryAt(depth - 1).getName();
            if (INTERMEDIATE_POP.equals(name)) {
                transitionInfo = tags.get(depth - 2);
            } else if (INTERMEDIATE_PUSH.equals(name)) {
                transitionInfo = tags.get(depth - 1);
            }
        }
        if (transitionInfo == null) {
            return null;
        }
        return transitionInfo.sharedElementName;
    }

    public void transitionReady() {
        final FragmentManager fragmentManager = fragmentManagerRef.get();
        if (fragmentManager == null) {
            return;
        }
        final int depth = fragmentManager.getBackStackEntryCount();
        if (depth == 0) {
            Log.w(TAG, "transitionReady called while back stack is empty.");
            return;
        }
        final FragmentManager.BackStackEntry backStackEntry =
                fragmentManager.getBackStackEntryAt(depth - 1);
        final String backStackName = backStackEntry.getName();
        final boolean isForward;
        final int underLevel;
        if (INTERMEDIATE_PUSH.equals(backStackName)) {
            underLevel = depth - 1;
            isForward = true;
        } else if (INTERMEDIATE_POP.equals(backStackName)) {
            underLevel = depth - 2;
            isForward = false;
        } else {
            Log.w(TAG, "transitionReady called while back stack is not in the intermediate state");
            return;
        }
        final FragmentTransitionInfo transitionInfo = tags.get(underLevel);
        if (transitionInfo == null) {
            // Ack! This isn't a valid state
            Log.w(TAG, "Invalid intermediate state found in back stack");
            return;
        }

        if (isForward) {
            // Going forward
            continueForward(transitionInfo);
        } else {
            // Intermediate popping back stack
            continueBackward();
        }
    }

    @Override
    public void onBackStackChanged() {
        final FragmentManager fragmentManager = fragmentManagerRef.get();
        if (fragmentManager == null) {
            return;
        }
        final int depth = fragmentManager.getBackStackEntryCount();
        if (depth == 0) {
            return;
        }
        final FragmentManager.BackStackEntry backStackEntry =
                fragmentManager.getBackStackEntryAt(depth - 1);
        if (INTERMEDIATE_PUSH.equals(backStackEntry.getName())) {
            final int underLevel = depth - 1;
            final FragmentTransitionInfo transitionInfo = tags.get(underLevel);
            if (transitionInfo == null) {
                // Ack! This isn't a valid state
                Log.w(TAG, "Invalid intermediate state found in back stack");
                return;
            }
            if (transitionInfo.sharedElement == null) {
                return; // Popping back. Ignore this.
            }
            final Fragment to = fragmentManager.findFragmentByTag(transitionInfo.toTag);
            final boolean delayTransition;
            if (to instanceof DelayedTransitionFragment) {
                delayTransition = ((DelayedTransitionFragment) to).isTransitionDelayed();
            } else {
                delayTransition = false;
            }

            if (!delayTransition) {
                continueForward(transitionInfo);
            }
        } else if (INTERMEDIATE_POP.equals(backStackEntry.getName())) {
            final int underLevel = depth - 2;
            final FragmentTransitionInfo transitionInfo = tags.get(underLevel);
            if (transitionInfo == null) {
                // Ack! This isn't a valid state
                Log.w(TAG, "Invalid intermediate state found in back stack");
                return;
            }
            if (transitionInfo.sharedElement != null) {
                // Going forward.
                transitionInfo.sharedElement = null;
                return;
            }
            final Fragment from = fragmentManager.findFragmentByTag(transitionInfo.fromTag);
            final boolean delayTransition;
            if (from instanceof DelayedTransitionFragment) {
                delayTransition = ((DelayedTransitionFragment) from).isTransitionDelayed();
            } else {
                delayTransition = false;
            }

            if (!delayTransition) {
                continueBackward();
            }
        }
    }

    private void continueForward(FragmentTransitionInfo transitionInfo) {
        final FragmentManager fragmentManager = fragmentManagerRef.get();
        if (fragmentManager == null) {
            return;
        }
        final Fragment from = fragmentManager.findFragmentByTag(transitionInfo.fromTag);
        final Fragment to = fragmentManager.findFragmentByTag(transitionInfo.toTag);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        View shared = transitionInfo.sharedElement.get();
        if (shared != null) {
            transaction.addSharedElement(shared, transitionInfo.sharedElementName);
        }
        transaction
                .addToBackStack(INTERMEDIATE_POP)
                .hide(from)
                .show(to)
                .commit();
        fragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .show(from)
                .remove(from)
                .commit();
    }

    private void continueBackward() {
        final FragmentManager fragmentManager = fragmentManagerRef.get();
        if (fragmentManager == null) {
            return;
        }
        fragmentManager.popBackStack();
        fragmentManager.popBackStackImmediate();
    }

    private static class FragmentTransitionInfo {
        public final String fromTag;
        public final String toTag;
        public WeakReference<View> sharedElement;
        public final String sharedElementName;

        public FragmentTransitionInfo(String from, String to, View sharedElement,
                                      String sharedElementName) {
            this.fromTag = from;
            this.toTag = to;
            this.sharedElement = new WeakReference<View>(sharedElement);
            this.sharedElementName = sharedElementName;
        }
    }

    public interface DelayedTransitionFragment {
        boolean isTransitionDelayed();
    }
}

