# ArgueSecure
Argumentation-based Risk Assessment tool based on previous research by Ionita et al: http://eprints.eemcs.utwente.nl/25041/01/Argumentations-support_for_Security_Requirements.pdf

> A tool which supports documenting the structured arguments and defeasibility relationships elicited as part of an informal argumentation game. In addition it recursively computes argument states and tags arguments with the components or assets they refer to.


## USAGE:
Conducting an argument-based RA requires little preparation. Any number of stakeholders, domain experts and/or security experts can participate, but should be split up in two teams: Attackers and Defenders. The method assumes the participants posses pre-existing knowledge of the Target of Assessment. Ideally, but not mandatory, some sort of system model or diagram should be agreed upon by the participants. Each assessment follows the workflow below:
  1.  Create New Risk and provide a brief name/description of it
  2.  Each Risk starts with an Attacker argument, describing an attack path or refining the risk description above.Each argument consists of a Claim, supported by one or more assumptions. Use CTRL+SPACE to switch between Claims and Assumptions.
  3.  Each Attacker argument may be counter-acted by a Defender argument, describing a mitigation, reduction or transfer of the previously described attack. In case the argument describes a Risk Transfer, the "Transfer"  checkbox should be ticked.
  4.  This back-and-forth rhetoric can continue until:
     - The Attacker team is unable or unwilling to counter-act the last Defender argument. This means the Risk has been eliminated.
     - The Defender team is unable or unwilling to counter-act the last Attacker argument. This means the (residual) Risk has been accepted.
  5.  If other Risks  can be identified, go back to step 1.

### Tips & Tricks:
- When to start a New Risk:
  - As soon as a new attack vector is identified (even if this attack vector untuitively corresponds to the same Risk and/or compromises the same asset), it is recommended to specify it as part of a New Risk to prevent long rounds.
- Contents of Attacker arguments:
  - Claims are attacks that are possible. 
  - If everybody agrees, no assumptions are needed. If anyone believes it’s impossible, he has to explain why (in which circumstances). 
  - The negations of these reasons are assumptions.
- Contents of Defender arguments: 
  - Claims are (parts of) attacks which are impossible. 
  - If everybody agrees, no assumptions are needed. If anyone believes it’s still possible, he has to explain why (in which circumstances).  
  - The negations of these reasons are assumptions.


## DEVELOPMENT PLAN
This is a first version, still missing some functionality so please don't remove the old spreadsheets. In the near future, we plan to add:
- extended keyword/search  functionality aimed at searching for and highlighting arguments that pertain to the same components/assets.
- various report generation options, such as a Risk Map, a list of Countermeasures to be implemented and a list of important components, which could serve as input for a TREsPASS model.
