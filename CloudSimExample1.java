package cloudsimproject;

import java.text.DecimalFormat;
import java.util.*;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.*;

public class CloudSimExample1 {

    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmlist;

    public static void main(String[] args) {
        Log.printLine("Starting CloudSimExample1...");

        try {
            int num_user = 1;
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;

            CloudSim.init(num_user, calendar, trace_flag);

            Datacenter datacenter0 = createDatacenter("Datacenter_0");
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            vmlist = new ArrayList<Vm>();
            int vmid = 0;
            int mips = 1000;
            long size = 10000;
            int ram = 512;
            long bw = 1000;
            int pesNumber = 1;
            String vmm = "Xen";

            Vm vm1 = new Vm(vmid++, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            Vm vm2 = new Vm(vmid++, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            Vm vm3 = new Vm(vmid++, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            Vm vm4 = new Vm(vmid++, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());

            vmlist.add(vm1);
            vmlist.add(vm2);
            vmlist.add(vm3);
            vmlist.add(vm4);
            broker.submitVmList(vmlist);

            cloudletList = new ArrayList<Cloudlet>();
            int id = 0;
            long length = 400000;
            long fileSize = 300;
            long outputSize = 300;
            UtilizationModel utilizationModel = new UtilizationModelFull();

            Cloudlet cloudlet1 = new Cloudlet(id++, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet1.setUserId(brokerId);
            Cloudlet cloudlet2 = new Cloudlet(id++, length * 2, pesNumber, fileSize * 2, outputSize / 3, utilizationModel, utilizationModel, utilizationModel);
            cloudlet2.setUserId(brokerId);
            Cloudlet cloudlet3 = new Cloudlet(id++, length / 2, pesNumber, fileSize * 3, outputSize * 3, utilizationModel, utilizationModel, utilizationModel);
            cloudlet3.setUserId(brokerId);
            Cloudlet cloudlet4 = new Cloudlet(id++, length / 3, pesNumber, fileSize / 3, outputSize / 2, utilizationModel, utilizationModel, utilizationModel);
            cloudlet4.setUserId(brokerId);
            Cloudlet cloudlet5 = new Cloudlet(id++, length * 3, pesNumber, fileSize / 2, outputSize / 4, utilizationModel, utilizationModel, utilizationModel);
            cloudlet5.setUserId(brokerId);
            Cloudlet cloudlet6 = new Cloudlet(id++, length / 4, pesNumber, fileSize * 4, outputSize * 4, utilizationModel, utilizationModel, utilizationModel);
            cloudlet6.setUserId(brokerId);
            Cloudlet cloudlet7 = new Cloudlet(id++, length * 4, pesNumber, fileSize, outputSize * 2, utilizationModel, utilizationModel, utilizationModel);
            cloudlet7.setUserId(brokerId);
            Cloudlet cloudlet8 = new Cloudlet(id++, length, pesNumber, fileSize / 4, outputSize / 3, utilizationModel, utilizationModel, utilizationModel);
            cloudlet8.setUserId(brokerId);

            cloudletList.add(cloudlet1);
            cloudletList.add(cloudlet2);
            cloudletList.add(cloudlet3);
            cloudletList.add(cloudlet4);
            cloudletList.add(cloudlet5);
            cloudletList.add(cloudlet6);
            cloudletList.add(cloudlet7);
            cloudletList.add(cloudlet8);

            broker.submitCloudletList(cloudletList);

            broker.bindCloudletToVm(cloudlet1.getCloudletId(), vm1.getId());
            broker.bindCloudletToVm(cloudlet2.getCloudletId(), vm2.getId());
            broker.bindCloudletToVm(cloudlet3.getCloudletId(), vm3.getId());
            broker.bindCloudletToVm(cloudlet4.getCloudletId(), vm4.getId());
            broker.bindCloudletToVm(cloudlet5.getCloudletId(), vm1.getId());
            broker.bindCloudletToVm(cloudlet6.getCloudletId(), vm2.getId());
            broker.bindCloudletToVm(cloudlet7.getCloudletId(), vm3.getId());
            broker.bindCloudletToVm(cloudlet8.getCloudletId(), vm4.getId());

            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            List<Cloudlet> newList = broker.getCloudletReceivedList();
            printCloudletList(newList);
            Log.printLine("CloudSimExample1 finished!");

        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happened");
        }
    }

    private static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<Host>();
        List<Pe> peList = new ArrayList<Pe>();

        int mipsPerPe = 1000;

        // Add 4 PEs of 1000 MIPS each
        for (int i = 0; i < 4; i++) {
            peList.add(new Pe(i, new PeProvisionerSimple(mipsPerPe)));
        }

        int hostId = 0;
        int ram = 8192; // RAM in MB
        long storage = 1000000; // Storage in MB
        int bw = 10000;

        hostList.add(new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage,
                peList, new VmSchedulerTimeShared(peList)));

        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

        try {
            return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static DatacenterBroker createBroker() {
        try {
            return new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void printCloudletList(List<Cloudlet> list) {
        DecimalFormat dft = new DecimalFormat("###.##");

        Log.printLine();
        Log.printLine("========== OUTPUT ==========");

        // Header with fixed width columns
        Log.printLine(String.format("%-12s %-10s %-15s %-8s %-10s %-12s %-12s",
                "Cloudlet ID", "STATUS", "Data center ID", "VM ID", "Time", "Start Time", "Finish Time"));

        for (Cloudlet cloudlet : list) {
            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.printLine(String.format("%-12d %-10s %-15d %-8d %-10s %-12s %-12s",
                        cloudlet.getCloudletId(),
                        "SUCCESS",
                        cloudlet.getResourceId(),
                        cloudlet.getVmId(),
                        dft.format(cloudlet.getActualCPUTime()),
                        dft.format(cloudlet.getExecStartTime()),
                        dft.format(cloudlet.getFinishTime())
                ));
            }
        }
    }
}
