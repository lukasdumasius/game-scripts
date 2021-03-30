package scripts;

import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSItem;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.ItemClicking;
import org.tribot.api2007.Combat;
import org.tribot.api.General;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

@ScriptManifest(name = "Nmz Script", authors = { "author" }, category = "default")
public class MyScript extends Script {

    private int rockRandomInt = 0;
    private boolean rockFlag=false;
    private int getAmountAbsorption() {
        int absorbption_amount = 0;
        RSInterfaceComponent rsInterfaceComponent = null;
        RSInterfaceChild rsInterfaceChild = Interfaces.get(202, 3);
        if(rsInterfaceChild != null){
            rsInterfaceComponent = rsInterfaceChild.getChild(5);
        }
        String absorption_amount_str = rsInterfaceComponent.getText();
        if( (rsInterfaceComponent != null) && (absorption_amount_str != null) ){
            try {
                absorbption_amount = Integer.parseInt(absorption_amount_str);
            }
            catch (NumberFormatException e)
            {
                absorbption_amount = 0;
            }
        }
        return absorbption_amount;
    }

    private void drinkPotions(){
        int doses_to_drink = (int)(Math.random() * 9)+10;						// drinks 10 -> 19 doses
        System.out.println("Doses to drink: " + doses_to_drink);
        while(doses_to_drink>0){
            General.sleep(700);
            System.out.println("Doses LEFT to drink: " + doses_to_drink);		//debugging

            RSItem[] absorption_1 = Inventory.find("Absorption (1)");
            RSItem[] absorption_2 = Inventory.find("Absorption (2)");
            RSItem[] absorption_3 = Inventory.find("Absorption (3)");
            RSItem[] absorption_4 = Inventory.find("Absorption (4)");
            int randomizationSeed = (int)(Math.random() * 4)+1;					// generate 1 -> 4

            if((absorption_1.length > 0) && (randomizationSeed >= 3)){			// Assumption: If the amount we hit
                absorption_1[0].click();											// is not available, we will quickly
                doses_to_drink--;													// trigger conditions to come back here
                continue;															// and 'reroll'
            }
            if((absorption_2.length > 0) && (randomizationSeed<=2)){
                absorption_2[0].click();
                doses_to_drink--;
                continue;
            }
            if((absorption_3.length > 0) && (randomizationSeed==1)){
                absorption_3[0].click();
                doses_to_drink--;
                continue;
            }
            if((absorption_4.length > 0) && (randomizationSeed==4)){
                absorption_4[0].click();
                doses_to_drink--;
                continue;
            }

            if( (absorption_1.length < 1) && (absorption_2.length < 1) && (absorption_3.length < 1) && (absorption_4.length < 1)){
                break;
            }


        }
    }
    private void absorbHandler(){
        if(getAmountAbsorption() == 0){			// Abosprotion is empty, must drink
            drinkPotions();
        }
        if(!absorption_scheduled){									// Absorption > 0
            if(getAmountAbsorption()<200){
                scheduled_abosrbption_amount=(int)(Math.random() * 115)+60;
                System.out.println("Scheduled absorb amount: " + scheduled_abosrbption_amount);
                absorption_scheduled=true;
            }
        }
        if( (absorption_scheduled == true) && (getAmountAbsorption() < scheduled_abosrbption_amount)){
            drinkPotions();
            absorption_scheduled=false;


        }
    }

    private void overloadHandler(){
        if(Skills.getActualLevel(SKILLS.STRENGTH) == Skills.getCurrentLevel(SKILLS.STRENGTH)){

            int randomizationSeed = (int)(Math.random() * 4)+1;           // Sleep for a random time.
            RSItem[] Overload_1 = Inventory.find("Overload (1)");		 //  TODO: 'schedule' it instead of blocking here
            RSItem[] Overload_2 = Inventory.find("Overload (2)");		//   ( would prevent player death)
            RSItem[] Overload_3 = Inventory.find("Overload (3)");
            RSItem[] Overload_4 = Inventory.find("Overload (4)");

            if( (Overload_1.length > 0) && (randomizationSeed>=3) ){
                General.sleep((int)(Math.random() * 20000)+10000);
                Overload_1[0].click();
                return;
            }
            if( (Overload_2.length > 0) && (randomizationSeed==4) ){
                General.sleep((int)(Math.random() * 20000)+10000);
                Overload_2[0].click();
                return;
            }
            if( (Overload_3.length > 0) && (randomizationSeed<=2) ){
                General.sleep((int)(Math.random() * 20000)+10000);
                Overload_3[0].click();
                return;
            }
            if( (Overload_4.length > 0) && (randomizationSeed==1) ){
                General.sleep((int)(Math.random() * 20000)+10000);
                Overload_4[0].click();
                return;
            }

            if( (Overload_1.length < 1) && (Overload_2.length < 1) && (Overload_3.length < 1) && (Overload_4.length < 1)){
                return;
            }
        }
        else{
            return; // no need to drink overload
        }
    }

    private void rockHelper(){
        if( (Combat.getHP() >= rockRandomInt) && (rockFlag == true)){	// Time to handle rock

            RSItem[] item_rock = Inventory.find("Dwarven rock cake");
            General.sleep((int)(Math.random() * 20000)+10000);			// Sleep for a random time.
            while(Combat.getHP()>1){
                if( (item_rock[0]!=null)){
                    if(Skills.getActualLevel(SKILLS.STRENGTH) == Skills.getCurrentLevel(SKILLS.STRENGTH)){
                        System.out.println("Rock cancelled, need overload");
                        return;
                    }
                    item_rock[0].click("Guzzle");
                    General.sleep(700);
                    System.out.println("Rock Handled");
                    rockFlag=false;
                }
                else{
                    System.out.println("Cannot find Rock");
                }
            }
        }
        return;														// else, return
    }

    private void rockEaterHandler(){
        if(Combat.getHP()>1 && rockFlag == false){
            rockRandomInt = (int)(Math.random() * 5)+1;
            rockFlag=true;

        }
    }

    private boolean absorption_scheduled = false;
    private int scheduled_abosrbption_amount = 0;

    @Override
    public void run() {
        General.println("Booting Up!");

        while (true) {
            General.sleep(100);			// Sleep to not chew up clock cycles
            absorbHandler();
            overloadHandler();
            rockEaterHandler();
            rockHelper();
            General.sleep(100);
            // rockRandomInt
        }

    }
}



////////////

