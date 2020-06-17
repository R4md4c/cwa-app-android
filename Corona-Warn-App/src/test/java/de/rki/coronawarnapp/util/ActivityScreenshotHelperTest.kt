package de.rki.coronawarnapp.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import de.rki.coronawarnapp.test.TestActivity
import de.rki.coronawarnapp.util.screenshots.ScreenshotCaptureBlockable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ActivityScreenshotHelperTest {

    @Test
    fun flagSecure_IsSet_WhenFragmentIsAttached() {
        val activityScreenshotHelper = ActivityScreenshotHelper("device")

        var flagSecureExists = false

        ActivityScenario.launch(TestActivity::class.java).use { scenario ->
            scenario.setupFragment(activityScreenshotHelper)

            scenario.onActivity { activity ->
                flagSecureExists = activity.window.flagSecureExists()
            }
        }

        Assert.assertEquals(true, flagSecureExists)
    }

    @Test
    fun flagSecure_IsNotSet_WhenBuildConfigFlavorIsdeviceForTesters() {
        val activityScreenshotHelper = ActivityScreenshotHelper("deviceForTesters")

        var flagSecureExists = false
        ActivityScenario.launch(TestActivity::class.java).use { scenario ->
            scenario.setupFragment(activityScreenshotHelper)

            scenario.onActivity { activity ->
                flagSecureExists = activity.window.flagSecureExists()
            }
        }

        Assert.assertEquals(false, flagSecureExists)
    }

    private fun Window.flagSecureExists(): Boolean =
        (attributes.flags and WindowManager.LayoutParams.FLAG_SECURE) == WindowManager.LayoutParams.FLAG_SECURE

    private fun ActivityScenario<TestActivity>.setupFragment(
        activityScreenshotHelper: ActivityScreenshotHelper
    ) {
        onActivity { activity ->
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                activityScreenshotHelper,
                true
            )
            val fragment = TestScreenshotFragment()
            activity.addFragment(fragment)
            Assert.assertEquals(true, fragment.isAdded)
        }
    }

    internal class TestScreenshotFragment : Fragment(), ScreenshotCaptureBlockable {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? = View(requireContext())
    }
}
