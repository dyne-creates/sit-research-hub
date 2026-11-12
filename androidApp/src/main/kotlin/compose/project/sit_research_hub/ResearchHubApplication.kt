package compose.project.sit_research_hub

import android.app.Application
import compose.project.sit_research_hub.data.local.AndroidPlatformContext

class ResearchHubApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidPlatformContext.application = this
    }
}

