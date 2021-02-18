package co.rsk.bitcoinj.script;

import co.rsk.bitcoinj.core.BtcECKey;
import co.rsk.bitcoinj.core.Sha256Hash;
import co.rsk.bitcoinj.core.VerificationException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FastBridgeErpRedeemScriptParserTest {

    private final List<BtcECKey> defaultFedBtcECKeyList = new ArrayList<>();
    private final List<BtcECKey> erpFedBtcECKeyList = new ArrayList<>();
    private final BtcECKey ecKey1 = BtcECKey.fromPrivate(BigInteger.valueOf(100));
    private final BtcECKey ecKey2 = BtcECKey.fromPrivate(BigInteger.valueOf(200));
    private final BtcECKey ecKey3 = BtcECKey.fromPrivate(BigInteger.valueOf(300));
    private final BtcECKey ecKey4 = BtcECKey.fromPrivate(BigInteger.valueOf(400));
    private final BtcECKey ecKey5 = BtcECKey.fromPrivate(BigInteger.valueOf(500));
    private final BtcECKey ecKey6 = BtcECKey.fromPrivate(BigInteger.valueOf(600));

    @Before
    public void setUp() {
        defaultFedBtcECKeyList.add(ecKey1);
        defaultFedBtcECKeyList.add(ecKey2);
        defaultFedBtcECKeyList.add(ecKey3);
        erpFedBtcECKeyList.add(ecKey4);
        erpFedBtcECKeyList.add(ecKey5);
        erpFedBtcECKeyList.add(ecKey6);
    }

    @Test
    public void extractStandardRedeemScript_fromFastBridgeErpRedeemScript() {
        Long csvValue = 100L;
        Sha256Hash derivationArgumentsHash = Sha256Hash.of(new byte[]{1});
        Script fastBridgeErpRedeemScript = RedeemScriptUtils.createFastBridgeErpRedeemScript(
            defaultFedBtcECKeyList,
            erpFedBtcECKeyList,
            csvValue,
            derivationArgumentsHash.getBytes()
        );

        Script standardRedeemScript = RedeemScriptUtils.createStandardRedeemScript(defaultFedBtcECKeyList);

        Script obtainedRedeemScript = FastBridgeErpRedeemScriptParser.extractStandardRedeemScript(
            fastBridgeErpRedeemScript.getChunks()
        );

        Assert.assertEquals(standardRedeemScript, obtainedRedeemScript);
    }

    @Test(expected = VerificationException.class)
    public void extractStandardRedeemScript_fromStandardRedeemScript_fail() {
        Script standardRedeemScript = RedeemScriptUtils.createStandardRedeemScript(defaultFedBtcECKeyList);

        FastBridgeErpRedeemScriptParser.extractStandardRedeemScript(standardRedeemScript.getChunks());
    }

    @Test
    public void createFastBridgeErpRedeemScript() {
        Script defaultFederationRedeemScript = RedeemScriptUtils.createStandardRedeemScript(defaultFedBtcECKeyList);
        Script erpFederationRedeemScript = RedeemScriptUtils.createStandardRedeemScript(erpFedBtcECKeyList);
        Long csvValue = 200L;
        Sha256Hash derivationArgumentsHash = Sha256Hash.of(new byte[]{1});

        Script expectedErpRedeemScript = RedeemScriptUtils.createFastBridgeErpRedeemScript(
            defaultFedBtcECKeyList,
            erpFedBtcECKeyList,
            csvValue,
            derivationArgumentsHash.getBytes()
        );

        Script obtainedRedeemScript = FastBridgeErpRedeemScriptParser.createFastBridgeErpRedeemScript(
            defaultFederationRedeemScript,
            erpFederationRedeemScript,
            csvValue,
            derivationArgumentsHash
        );

        Assert.assertEquals(expectedErpRedeemScript, obtainedRedeemScript);
    }

    @Test(expected = VerificationException.class)
    public void createFastBridgeErpRedeemScript_invalidDefaultFederationRedeemScript() {
        Script defaultFederationRedeemScript = RedeemScriptUtils.createCustomRedeemScript(defaultFedBtcECKeyList);
        Script erpFederationRedeemScript = RedeemScriptUtils.createStandardRedeemScript(erpFedBtcECKeyList);
        Long csvValue = 200L;
        Sha256Hash derivationArgumentsHash = Sha256Hash.of(new byte[]{1});

        FastBridgeErpRedeemScriptParser.createFastBridgeErpRedeemScript(
            defaultFederationRedeemScript,
            erpFederationRedeemScript,
            csvValue,
            derivationArgumentsHash
        );
    }

    @Test(expected = VerificationException.class)
    public void createFastBridgeErpRedeemScript_invalidErpFederationRedeemScript() {
        Script defaultFederationRedeemScript = RedeemScriptUtils.createStandardRedeemScript(defaultFedBtcECKeyList);
        Script erpFederationRedeemScript = RedeemScriptUtils.createCustomRedeemScript(erpFedBtcECKeyList);
        Long csvValue = 200L;
        Sha256Hash derivationArgumentsHash = Sha256Hash.of(new byte[]{1});

        FastBridgeErpRedeemScriptParser.createFastBridgeErpRedeemScript(
            defaultFederationRedeemScript,
            erpFederationRedeemScript,
            csvValue,
            derivationArgumentsHash
        );
    }

    @Test
    public void isFastBridgeErpFed() {
        Script fastBridgeErpRedeemScript = RedeemScriptUtils.createFastBridgeErpRedeemScript(
            defaultFedBtcECKeyList,
            erpFedBtcECKeyList,
            200L,
            Sha256Hash.of(new byte[]{1}).getBytes()
        );

        Assert.assertTrue(FastBridgeErpRedeemScriptParser.isFastBridgeErpFed(fastBridgeErpRedeemScript.getChunks()));
    }

    @Test
    public void isFastBridgeErpFed_falseWithCustomRedeemScript() {
        Script customRedeemScript = RedeemScriptUtils.createCustomRedeemScript(defaultFedBtcECKeyList);

        Assert.assertFalse(FastBridgeErpRedeemScriptParser.isFastBridgeErpFed(customRedeemScript.getChunks()));
    }
}
