package amak;

import application.Controller;	
import application.ExceptionHandler;
import business.Blob;
import business.Critere;


public class Immaginaire extends BlobAgent {

	public Immaginaire(MyAMAS amas, Blob b, Controller controller) {
		super(amas, b, controller);
	}

	// Fait juste un appel a computeCriticalityInTideal.
	@Override
	protected double computeCriticality() {
		return (computeCriticalityInTideal());
	}

	// @Override
	protected void action_se_deplacer(float delta) {
		double[] tmp = getAmas().getEnvironment().nouvellesCoordonnees(this, Math.random() * delta * 3.0, pastDirection);
		blob.setCoordonnee(tmp);
		currentAction = Action.SE_DEPLACER;
		directGeneral[0] = 0.6 * pastDirection[0] + 0.4 * directGeneral[0];
		directGeneral[1] = 0.6 * pastDirection[1] + 0.4 * directGeneral[1];
	}

	@Override
	protected void onDecideAndAct() {
		float delta = System.currentTimeMillis() - tps;
		tps = System.currentTimeMillis();
		try {
			{
				action_se_deplacer(delta/1000.f);
				nbChangements = 0;
				updateColor();
				currentAction = Action.RESTER; // to initialise
				BlobAgent agentNeedingHelp = super.getMoreCriticalAgent();
				Critere most_critic = Most_critical_critere(agentNeedingHelp.getCriticite());

				switch (most_critic) {
				case Isolement:
					// too many neighboors -> criticite.ISOLEMENT<0 -> I have to kill myself
					if (criticite[Critere.Isolement.getValue()] < -3.f) {
						action_se_suicider();
					}
					else if (criticite[Critere.Isolement.getValue()] > 3.f)
						action_creer();
					break;

				case Stabilite_etat:

					break;

				case Stabilite_position:
					action_se_deplacer((float)delta/1000.f);
					break;

				case Heterogeneite:
					if (criticite[Critere.Heterogeneite.getValue()] > 0)
						action_changerCouleur();
					else {
						Blob v = getPlusProcheVoisin();
						if (v == null) {
							action_changerCouleur();
						} else {					
							action_changerCouleur(getPlusProcheVoisin().getMaSuperCouleurPreferee());
						}
					}
					break;

				default:
					break;
				}
			}

			super.onDecideAndAct();

		} catch (Exception e) {
			ExceptionHandler eh = new ExceptionHandler();
			eh.showError(e);
		}
	}

}
