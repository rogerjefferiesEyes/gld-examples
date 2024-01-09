package longpage;

import com.applitools.ICheckSettings;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

public class LongPageTest {
    public static void main(String[] args) throws Exception {

        WebDriver driver = new ChromeDriver();

        Eyes eyes = new Eyes();

        eyes.setLogHandler(new StdoutLogHandler(true));

        try {
            int viewportWidth = 1200;
        	
            WebDriver eyesDriver = eyes.open(driver, "Applitools", "Long page testing example - Java", new RectangleSize(viewportWidth, 600));

            eyesDriver.get("https://help.applitools.com/hc/en-us/articles/360041392472-Long-page-testing");
            
            // Save original configuration
            StitchMode originalStitchMode = eyes.getStitchMode();
            boolean originalFullPageMode = eyes.getForceFullPageScreenshot();
            // Change configuration
            eyes.setForceFullPageScreenshot(true);
            eyes.setStitchMode(StitchMode.CSS);

            // Get the size and location of the main element
            WebElement element = eyesDriver.findElement(By.cssSelector("html"));
            //Dimension size = element.getSize();
            Dimension size = new Dimension(
            		viewportWidth, 
            		Integer.parseInt(element.getAttribute("scrollHeight")));
            System.out.println("width: " + size.width);
            System.out.println("height: " + size.height);
            Point location = element.getLocation();

            // Set the size for each Region, within limit of 15k pixels
            int maxLengthOfEachPart = 10000; // Section/Region Size: 10k pixels

            // Divide the element into "chunks" of size under 15k pixels,
            // and create a list of Targets
            Region region;
            ICheckSettings[] targets = new ICheckSettings[(size.height/maxLengthOfEachPart)+1];
            for (int i = location.y, section = 0; i < location.y + size.height; i += maxLengthOfEachPart, section++)
            {
                if ((location.y + size.height) > i + maxLengthOfEachPart)
                {
                    region = new Region(location.x, i, size.width, maxLengthOfEachPart);
                }
                else
                {
                    region = new Region(location.x, i, size.width, (location.y + size.height) - i);
                }
                targets[section] = Target.region(region);
            }

            // Capture element by steps (array of Regions)
            eyes.check(targets);

            // Restore original configuration
            eyes.setForceFullPageScreenshot(originalFullPageMode);
            eyes.setStitchMode(originalStitchMode);

            // End the test.
            eyes.close();

        }
        finally {
            driver.quit();

            eyes.abortIfNotClosed();
            System.exit(0);
        }
    }
}
