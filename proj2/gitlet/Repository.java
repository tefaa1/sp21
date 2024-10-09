package gitlet;

import jdk.jshell.execution.Util;

import javax.sql.rowset.Joinable;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.AllPermission;
import java.security.KeyPair;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

import static gitlet.Utils.*;
import static gitlet.repoUtils.*;


/** Represents a gitlet repository.
 *  have all methods that represent the commands of gitlet
 *
 *  does at a high level.
 *
 *  @author mohamed abdellatif
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    private static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    private static final File GITLET_DIR = join(CWD, ".gitlet");
    private static File commits = join(GITLET_DIR, "commits");
    private static File blobs = join(GITLET_DIR, "blobs");
    private static File head = join(GITLET_DIR, "head");
    private static File branches = join(GITLET_DIR, "branches");
    private static File branchesSet = join(GITLET_DIR, "branches set");
    /**
     * The staged file will contain a map where the key is the name of the file
     * ,and the value is the hash code of the file.
     */
    private static File blobsMap = join(GITLET_DIR, "blobsMap");
    private static File stagedForAddFiles = join(GITLET_DIR, "staged add files");
    private static File stagedForRemoveFiles = join(GITLET_DIR, "staged remove files");

    public static void init() {
        GITLET_DIR.mkdir();
        commits.mkdir();
        blobs.mkdir();
        branches.mkdir();
        if (commits.list().length == 0) {
            Commit c = new Commit("initial commit", null, null, "-1");
            String hash = sha1(serialize(c));
            c.setId(hash);
            File com = join(commits, hash);
            makeNewFile(com);
            writeObject(com, c);
            Branch master = new Branch("master", hash, true);
            File Master = join(branches, master.getName());
            makeNewFile(Master);
            writeObject(Master, master);
            makeNewFile(head);
            writeContents(head, master.getName());
            HashSet<String> branch = new HashSet<>();
            branch.add(master.getName());
            writeObject(branchesSet, branch);
            makeNewFile(blobsMap);
            makeNewFile(stagedForAddFiles);
            makeNewFile(stagedForRemoveFiles);
            writeObject(blobsMap, new HashMap<String, String>());
            writeObject(stagedForAddFiles, new HashMap<String, String>());
            writeObject(stagedForRemoveFiles, new HashSet<String>());
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }

    public static void add(String addedFile) {
        checkRepo(GITLET_DIR);
        File fileByUser = join(CWD, addedFile);
        HashMap<String, String> stagedAdd = readObject(stagedForAddFiles, HashMap.class);
        HashSet<String> stagedRem = readObject(stagedForRemoveFiles, HashSet.class);
        HashMap<String, String> checkBlobs = readObject(blobsMap, HashMap.class);
        if (!fileByUser.exists()) {
            if (checkBlobs.containsKey(addedFile)) {
                HashSet<String> stagedRemove = readObject(stagedForRemoveFiles, HashSet.class);
                stagedRemove.add(addedFile);
                stagedAdd.remove(addedFile);
                checkBlobs.remove(addedFile);
                writeObject(blobsMap, checkBlobs);
                writeObject(stagedForRemoveFiles, stagedRemove);
                writeObject(stagedForAddFiles, stagedAdd);
            } else {
                System.out.println("File does not exist.");
            }
        } else {
            String newHash = HashCode(fileByUser);
            if (stagedAdd.containsKey(addedFile)) { // no commit
                String oldHash = stagedAdd.get(addedFile);
                if (!oldHash.equals(newHash)) {
                    File blob = join(blobs, oldHash);
                    blob.delete();
                    File newBlob = join(blobs, newHash);
                    writeContents(newBlob, readContents(fileByUser));
                    stagedAdd.replace(addedFile, newHash);
                    checkBlobs.replace(addedFile, newHash);
                    writeObject(blobsMap, checkBlobs);
                    writeObject(stagedForAddFiles, stagedAdd);
                }
            } else {
                // you may just make a commit
                if (checkBlobs.containsKey(addedFile)) {
                    if (!checkBlobs.get(addedFile).equals(newHash)) {
                        File newBlob = join(blobs, newHash);
                        writeContents(newBlob, readContents(fileByUser));
                        stagedAdd.put(addedFile, newHash);
                        checkBlobs.replace(addedFile, newHash);
                        writeObject(blobsMap, checkBlobs);
                        writeObject(stagedForAddFiles, stagedAdd);
                    }
                } else {
                    // you might never add this file
                    File newBlob = join(blobs, newHash);
                    writeContents(newBlob, readContents(fileByUser));
                    stagedAdd.put(addedFile, newHash);
                    if (stagedRem.contains(addedFile)) {
                        stagedRem.remove(addedFile);
                    }
                    checkBlobs.put(addedFile, newHash);
                    writeObject(blobsMap, checkBlobs);
                    writeObject(stagedForAddFiles, stagedAdd);
                    writeObject(stagedForRemoveFiles, stagedRem);
                }
            }
        }
    }

    public static void commit(String message) {
        checkRepo(GITLET_DIR);
        HashMap<String, String> stagedAdd = readObject(stagedForAddFiles, HashMap.class);
        HashSet<String> stagedRemove = readObject(stagedForRemoveFiles, HashSet.class);
        if (stagedAdd.isEmpty() && stagedRemove.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        HashMap<String, String> checkBlobs = readObject(blobsMap, HashMap.class);
        Branch HEAD = readObject(join(branches, readContentsAsString(head)), Branch.class);
        Commit parent = readObject(join(commits, HEAD.getID()), Commit.class);
        Commit com = new Commit(message, parent, null, "-1");
        com.setRefs(checkBlobs);
        String hash = sha1(serialize(com));
        com.setId(hash);
        HEAD.setID(hash);
        File newCommit = join(commits, hash);
        makeNewFile(newCommit);
        stagedRemove.clear();
        stagedAdd.clear();
        writeObject(newCommit, com);
        writeObject(join(branches, readContentsAsString(head)), HEAD);
        writeObject(stagedForAddFiles, stagedAdd);
        writeObject(stagedForRemoveFiles, stagedRemove);
    }

    public static void rm(String rem) {
        checkRepo(GITLET_DIR);
        HashMap<String, String> stagedAdd = readObject(stagedForAddFiles, HashMap.class);
        HashSet<String> stagedRemove = readObject(stagedForRemoveFiles, HashSet.class);
        HashMap<String, String> checkBlobs = readObject(blobsMap, HashMap.class);
        if (checkBlobs.containsKey(rem)) {
            stagedAdd.remove(rem);
            stagedRemove.add(rem);
            checkBlobs.remove(rem);
            File del = join(CWD, rem);
            restrictedDelete(del);
            writeObject(blobsMap, checkBlobs);
            writeObject(stagedForAddFiles, stagedAdd);
            writeObject(stagedForRemoveFiles, stagedRemove);
        } else {
            System.out.println("No reason to remove the file.");
        }
    }

    public static void log() {
        checkRepo(GITLET_DIR);
        File HEAD = join(branches, readContentsAsString(head));
        Branch branch = readObject(HEAD, Branch.class);
        Commit Head = readObject(join(commits, branch.getID()), Commit.class);
        while (Head != null) {
            System.out.println("===\ncommit " + Head.getId());
            if (!Head.getMerge().equals("-1")) System.out.println("Merge: " + Head.getMerge());
            System.out.println("Date " + Head.getTimeStamp());
            System.out.println(Head.getMessage() + "\n");
            Head = Head.getParent();
        }
    }

    public static void globalLog() {
        checkRepo(GITLET_DIR);
        File[] files = join(commits).listFiles();
        for (File it : files) {
            Commit Head = readObject(it, Commit.class);
            System.out.println("===");
            System.out.println("commit " + Head.getId());
            if (!Head.getMerge().equals("-1")) System.out.println("Merge: " + Head.getMerge());
            System.out.println("Date " + Head.getTimeStamp());
            System.out.println(Head.getMessage() + "\n");
        }
    }

    public static void find(String message) {
        checkRepo(GITLET_DIR);
        File[] files = join(commits).listFiles();
        boolean f = true;
        for (File it : files) {
            Commit Head = readObject(it, Commit.class);
            if (Head.getMessage().equals(message)) {
                f = false;
                System.out.println(Head.getId());
            }
        }
        if (f) System.out.println("Found no commit with that message.");
    }

    public static void status() {
        checkRepo(GITLET_DIR);
        HashSet<String> branch = readObject(branchesSet, HashSet.class);
        String cur = readContentsAsString(head);
        System.out.println("=== Branches ===");
        for (String it : branch) {
            if (it.equals(cur)) {
                System.out.print("*");
            }
            System.out.println(it);
        }
        HashMap<String, String> stagedAdd = readObject(stagedForAddFiles, HashMap.class);
        HashSet<String> stagedRem = readObject(stagedForRemoveFiles, HashSet.class);
        System.out.println("\n=== Staged Files ===");
        for (String it : stagedAdd.keySet()) {
            System.out.println(it);
        }
        System.out.println("\n=== Removed Files ===");
        for (String it : stagedRem) {
            System.out.println(it);
        }
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        HashMap<String, String> allFiles = new HashMap<>();
        HashMap<String, String> checkBlobs = readObject(blobsMap, HashMap.class);
        getFiles(allFiles, CWD, join(CWD, ".gitlet"));
        checkBlobs.forEach((key, value) -> {
            if (allFiles.containsKey(key)) {
                if (!value.equals(allFiles.get(key))) {
                    System.out.println(key + " (modified)");
                }
                allFiles.remove(key);
            } else {
                System.out.println(key + " (deleted)");
            }
        });
        System.out.println("\n=== Untracked Files ===");
        allFiles.forEach((key, value) -> {
            System.out.println(key);
        });
    }

    public static void checkoutWithName(String name) {
        checkRepo(GITLET_DIR);
        String headName = readContentsAsString(head);
        Branch head = readObject(join(branches, headName), Branch.class);
        Commit com = readObject(join(commits, head.getID()), Commit.class);
        checkoutWithId(com.getId(), name);
    }

    public static void checkoutWithId(String id, String name) {
        checkRepo(GITLET_DIR);
        File fileCommit = join(commits, id);
        if (!fileCommit.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit Head = readObject(fileCommit, Commit.class);
        if (Head.geRefs().containsKey(name)) {
            String hash = Head.geRefs().get(name);
            File oldVersion = join(blobs, hash);
            File newVersion = join(CWD, name);
            createPathIfNotExists(name);
            writeContents(newVersion, readContents(oldVersion));
            HashMap<String, String> stagedAdd = readObject(stagedForAddFiles, HashMap.class);
            stagedAdd.remove(name);
            writeObject(stagedForAddFiles, stagedAdd);
        } else {
            System.out.println("File does not exist in that commit.");
        }
    }

    public static void checkoutWithBranch(String name) {
        checkRepo(GITLET_DIR);
        HashSet<String> branchSet = readObject(branchesSet, HashSet.class);
        if (branchSet.contains(name)) {
            if (name.equals(readContentsAsString(head))) {
                System.out.println("No need to checkout the current branch.");
            } else {
                Branch branch = readObject(join(branches, name), Branch.class);
                bringCommit(branch.getID());
                writeContents(head, name);
            }
        } else {
            System.out.println("No such branch exists.");
        }
    }

    private static void bringCommit(String ID) {
        HashMap<String, String> allFiles = new HashMap<>();
        getFiles(allFiles, CWD, join(CWD, ".gitlet"));
        HashMap<String, String> stagedAdd = readObject(stagedForAddFiles, HashMap.class);
        HashSet<String> stagedRemove = readObject(stagedForRemoveFiles, HashSet.class);
        HashMap<String, String> checkBlobs = readObject(blobsMap, HashMap.class);
        if (!checkBlobs.equals(allFiles) || !stagedAdd.isEmpty() || !stagedRemove.isEmpty()) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        } else {
            deleteFiles(CWD);
            checkBlobs.clear();
            Commit com = readObject(join(commits, ID), Commit.class);
            HashMap<String, String> refs = com.geRefs();
            refs.forEach((key, value) -> {
                checkBlobs.put(key, value);
                createPathIfNotExists(key);
                File dest = join(CWD, key);
                File source = join(blobs, value);
                makeNewFile(dest);
                writeContents(dest, readContents(source));
            });
            writeObject(blobsMap, checkBlobs);
        }
    }

    public static void branch(String name) {
        checkRepo(GITLET_DIR);
        HashSet<String> allBranches = readObject(branchesSet, HashSet.class);
        if (allBranches.contains(name)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        Branch HEAD = readObject(join(branches, readContentsAsString(head)), Branch.class);
        Branch branch = new Branch(name, HEAD.getID(), false);
        allBranches.add(name);
        File file = join(branches, name);
        makeNewFile(file);
        writeObject(file, branch);
        writeObject(branchesSet, allBranches);
    }

    public static void remBranch(String name) {
        checkRepo(GITLET_DIR);
        HashSet<String> allBranches = readObject(branchesSet, HashSet.class);
        if (!allBranches.contains(name)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        File branchFile = join(branches, readContentsAsString(head));
        Branch branch = readObject(branchFile, Branch.class);
        if (branch.getName().equals(name)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        allBranches.remove(name);
        File delBranch = join(branches, name);
        delBranch.delete();
        writeObject(branchesSet, allBranches);
    }

    public static void reset(String ID) {
        checkRepo(GITLET_DIR);
        File file = join(commits, ID);
        if (!file.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        bringCommit(ID);
        Branch branch = readObject(join(branches, readContentsAsString(head)), Branch.class);
        branch.setID(ID);
        writeObject(join(branches, readContentsAsString(head)), branch);
    }

    public static void merge(String name) {
        checkRepo(GITLET_DIR);
        HashMap<String, String> stagedAdd = readObject(stagedForAddFiles, HashMap.class);
        HashSet<String> stagedRemove = readObject(stagedForRemoveFiles, HashSet.class);
        if (!stagedAdd.isEmpty() || !stagedRemove.isEmpty()) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
        File file = join(branches, name);
        if (!file.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        Branch curBranch = readObject(join(branches, readContentsAsString(head)), Branch.class);
        Branch givBranch = readObject(join(branches, name), Branch.class);
        if (curBranch.getName().equals(givBranch.getName())) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        HashMap<String, String> allFiles = new HashMap<>();
        getFiles(allFiles, CWD, join(CWD, ".gitlet"));
        HashMap<String, String> checkBlobs = readObject(blobsMap, HashMap.class);
        if (!checkBlobs.equals(allFiles)) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
        Commit curCommit = readObject(join(commits, curBranch.getID()), Commit.class);
        Commit givCommit = readObject(join(commits, givBranch.getID()), Commit.class);
        String ID = splitPoint(new HashSet<>(), new HashSet<>(), curCommit, givCommit); // split Point (LCA)
        if (ID.equals(givCommit.getId())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (ID.equals(curCommit.getId())) {
            checkoutWithBranch(name);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        HashMap<String, String> givRefs = givCommit.geRefs();
        HashMap<String, String> curRefs = curCommit.geRefs();
        Commit idCommit = readObject(join(commits, ID), Commit.class);
        HashMap<String, String> idRefs = idCommit.geRefs();
        deleteFiles(CWD);
        checkBlobs.clear();
        givRefs.forEach((key, value) -> {
            if (curRefs.containsKey(key)) {
                if (value.equals(curRefs.get(key))) {
                    // 3
                    createPathIfNotExists(key);
                    File source = join(blobs, value);
                    File dest = join(key);
                    makeNewFile(dest);
                    writeContents(dest, readContents(source));
                    checkBlobs.put(key, value);
                    writeObject(blobsMap, checkBlobs);
                } else {
                    if (value.equals(idRefs.get(key))) {
                        // 2
                        createPathIfNotExists(key);
                        File source = join(blobs, value);
                        File dest = join(key);
                        makeNewFile(dest);
                        writeContents(dest, readContents(source));
                        checkBlobs.put(key, value);
                        writeObject(blobsMap, checkBlobs);
                    } else if (curRefs.get(key).equals(idRefs.get(key))) {
                        // 1
                        createPathIfNotExists(key);
                        File source = join(blobs, curRefs.get(key));
                        File dest = join(key);
                        makeNewFile(dest);
                        writeContents(dest, readContents(source));
                        checkBlobs.put(key, curRefs.get(key));
                        writeObject(blobsMap, checkBlobs);
                    } else {
                        // 8
                        String s = "<<<<<<< HEAD\n";
                        File curBlob = join(blobs, curRefs.get(key));
                        if (curBlob.exists()) {
                            s += readContents(curBlob);
                        }
                        s += "\n=======";
                        File givBlob = join(blobs, value);
                        if (givBlob.exists()) {
                            s += readContents(givBlob);
                        }
                        s += "\n>>>>>>>";
                        createPathIfNotExists(key);
                        File dest = join(key);
                        makeNewFile(dest);
                        writeContents(dest, s);
                        File newBlob = join(blobs, HashCode(dest));
                        makeNewFile(newBlob);
                        writeContents(newBlob, s);
                        checkBlobs.put(key, HashCode(dest));
                        writeObject(blobsMap, checkBlobs);
                    }
                }
                givRefs.remove(key);
            } else {
                if (idRefs.containsKey(key)) {
                    if (!idRefs.get(key).equals(value)) {
                        // 8
                        String s = "<<<<<<< HEAD\n=======\n";
                        File givBlob = join(blobs, value);
                        s += readContents(givBlob);
                        s += "\n >>>>>>>";
                        createPathIfNotExists(key);
                        File dest = join(key);
                        makeNewFile(dest);
                        writeContents(dest, s);
                        File newBlob = join(blobs, HashCode(dest));
                        makeNewFile(newBlob);
                        writeContents(newBlob, s);
                        checkBlobs.put(key, HashCode(dest));
                        writeObject(blobsMap, checkBlobs);
                    }
                } else {
                    // 5
                    createPathIfNotExists(key);
                    File dest = join(key);
                    makeNewFile(dest);
                    File source = join(blobs, value);
                    writeContents(dest, readContents(source));
                    checkBlobs.put(key, HashCode(dest));
                    writeObject(blobsMap, checkBlobs);
                }
            }
        });
        curRefs.forEach((key, value) -> {
            if (idRefs.containsKey(key)) {
                if (!idRefs.get(key).equals(value)) {
                    // 8
                    String s = "<<<<<<< HEAD\n";
                    File curBlob = join(blobs, curRefs.get(key));
                    s += readContents(curBlob);
                    s += "\n=======\n>>>>>>>";
                    createPathIfNotExists(key);
                    File dest = join(key);
                    makeNewFile(dest);
                    writeContents(dest, s);
                    File newBlob = join(blobs, HashCode(dest));
                    makeNewFile(newBlob);
                    writeContents(newBlob, s);
                    checkBlobs.put(key, HashCode(dest));
                    writeObject(blobsMap, checkBlobs);
                }
            } else {
                createPathIfNotExists(key);
                File source = join(blobs, value);
                File dest = join(key);
                makeNewFile(dest);
                writeContents(dest, readContents(source));
                checkBlobs.put(key, value);
                writeObject(blobsMap, checkBlobs);
                // 4
            }
        });
        String msg = "Merged " + readContentsAsString(head) + " into " + name;
        String merge = curCommit.getId().substring(0, 7) + " " + givCommit.getId().substring(0, 7);
        Commit com = new Commit(msg, curCommit, givCommit, merge);
        com.setId(sha1(serialize(com)));
        File dest = join(commits, com.getId());
        makeNewFile(dest);
        writeObject(dest, com);
        // hahahahahahhahahahahahhaaaah finallllyyyyyyyy
    }
}
