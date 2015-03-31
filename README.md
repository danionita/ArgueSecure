# ArgueSecure

ArgueSecure allows you to build and maintain a list of risks with the following structure
- Category: \<A category of risks>
  - R1: \<a risk>
    - (sword) C1: \<Claim made by an attacker about the existence of an attack path>
      - A A1.1: \<An assumption of the claim>
      - A A1.2: \<Another assumption of the claim>
    -(shield) C2: \<Claim made by a defender,that partly or completely defeats the attacker’s claim by pointing out that an attacker’s assumption is probasbly, or certainly, false>
      - A  A2.1: \<An Assumption of the defender’s claim, e.g. about a mitigation that alresdy exists or thst will be implemented.>
   - (sword) C3: \<Renewed claim of the attacker that bypasses the defender’s argument>
      - A A2.1: \<An assumption of this renewed claim>
   - Etc.
  - R2: etc.
- Category: etc.

Defender’s arguments can refer to components or architectural decisions that reduce a risk, and to decisions to transfer some risk to a third party (e.g. to an insurance company or to a customer).
If a risk  ends with all attacker’s claims partly or completely defeated, then all attack paths claimed by attackers in this argument have been partly or completely mitigated, or transferred to third parties. Risks that are undefeated, are accepted by the defender. 
The tool allows the production of lists of mitigations per risk, and of risks per mitigation. It provides a memory of the reasons why mitigations have been introduced, and which risks have been considered for mitigation.


## USAGE:
Conducting an argument-based RA requires little preparation. Any number of stakeholders, domain experts and/or security experts can participate, but should be split up in two teams: Attackers and Defenders. The method assumes the participants posses pre-existing knowledge of the Target of Assessment. Ideally, but not mandatory, some sort of system model or diagram should be agreed upon by the participants. 
The preferred process to use the tool is as follows:
  1.  Create New Risk and give it a name
  2.  Create a New Risk under this Category, and provide a brief name/description of it
  3.  Each Risk starts with an Attacker argument, describing an attack path or refining the Risk. Each argument consists of a Claim, supported by one or more assumptions. Use CTRL+SPACE to switch between entering Claims and Assumptions.
  4.	Each Attacker argument may be countered by a Defender argument, describing a mitigation, reduction or transfer of the Risk.
  5.	This back-and-forth rhetoric can continue until: 
    o	The Attacker team is unable or unwilling to counter the last Defender argument. This means the Risk has been mitigated sufficiently for this Attacker.
    o	The Defender team is unable or unwilling to counter the last Attacker argument. This means the (residual) Risk has been accepted.
  6.	If other Risks can be identified under this Cetegory, go back to step 2 and create a new Risk.
  7.	I a new Risk Category can be identified, go back to step 1.
  8.	At any time during the assessment, Defender arguments can be marked as "Implemented" (if they describe existing risk countermeasures) and/or "Transfer" (if they descibe a risk transfer).

### Color codes ###
- BLACK:  the color of all elements except Claims
- GREEN/RED: only applies to Claims.  Claims start out as green and turn red once defeated. Then, turn green again once their counter-arguments has bee defeated and so on.

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
In the near future, we plan to add extended keyword/search  functionality aimed at searching for and highlighting arguments that pertain to the same components/assets.



> ArgueSecure is based on research by Ionita et al: >http://eprints.eemcs.utwente.nl/25041/01/Argumentations-support_for_Security_Requirements.pdf
