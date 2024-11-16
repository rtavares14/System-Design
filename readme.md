# 3D Print Scheduler

This program was written to make deciding which print to start easier. 
It does not directly control any 3d printer, instead it keeps track of all the available prints and
chooses the most logical print to do next.

This is because changing a spool is time intensive and if there are a few orders using the same color it is 
far more efficient to print those in order.

Standard operation procedure is to just add all ordered prints to the queue and start the queue. Then use the
presented list and start up the printers with the correct prints. When a printer is done I will tell the program,
and it selects a new prints for that printer if one is available.

The entire interface can be controlled using only the numberpad, this is important as it speeds up interfacing
with this program for me. The computer is on a simple desk right next to the printers,
so I can quickly process prints and start them.