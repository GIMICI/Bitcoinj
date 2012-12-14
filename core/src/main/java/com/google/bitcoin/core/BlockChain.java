/**
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.bitcoin.core;

import com.google.bitcoin.store.BlockStore;
import com.google.bitcoin.store.BlockStoreException;

import java.util.ArrayList;
import java.util.List;

/**
 * A BlockChain forms a usable BlockChain class which implements basic SPV and storage using a {@link BlockStore}
 */
public class BlockChain extends AbstractBlockChain {
    /** Keeps a map of block hashes to StoredBlocks. */
    protected final BlockStore blockStore;

    /**
     * Constructs a BlockChain connected to the given wallet and store. To obtain a {@link Wallet} you can construct
     * one from scratch, or you can deserialize a saved wallet from disk using {@link Wallet#loadFromFile(java.io.File)}
     * <p/>
     *
     * For the store you can use a {@link com.google.bitcoin.store.MemoryBlockStore} if you don't care about saving the downloaded data, or a
     * {@link com.google.bitcoin.store.BoundedOverheadBlockStore} if you'd like to ensure fast startup the next time you run the program.
     */
    public BlockChain(NetworkParameters params, Wallet wallet, BlockStore blockStore) throws BlockStoreException {
        this(params, new ArrayList<BlockChainListener>(), blockStore);
        if (wallet != null)
            addWallet(wallet);
    }

    /**
     * Constructs a BlockChain that has no wallet at all. This is helpful when you don't actually care about sending
     * and receiving coins but rather, just want to explore the network data structures.
     */
    public BlockChain(NetworkParameters params, BlockStore blockStore) throws BlockStoreException {
        this(params, new ArrayList<BlockChainListener>(), blockStore);
    }

    /**
     * Constructs a BlockChain connected to the given list of listeners and a store.
     */
    public BlockChain(NetworkParameters params, List<BlockChainListener> wallets,
                      BlockStore blockStore) throws BlockStoreException {
        super(params, wallets, blockStore);
        this.blockStore = blockStore;
    }

    @Override
    protected StoredBlock addToBlockStore(StoredBlock storedPrev, Block blockHeader, TransactionOutputChanges txOutChanges)
            throws BlockStoreException, VerificationException {
        StoredBlock newBlock = storedPrev.build(blockHeader);
        blockStore.put(newBlock);
        return newBlock;
    }
    
    @Override
    protected StoredBlock addToBlockStore(StoredBlock storedPrev, Block blockHeader)
            throws BlockStoreException, VerificationException {
        StoredBlock newBlock = storedPrev.build(blockHeader);
        blockStore.put(newBlock);
        return newBlock;
    }

    @Override
    protected boolean shouldVerifyTransactions() {
        return false;
    }

    @Override
    protected TransactionOutputChanges connectTransactions(int height, Block block) {
        // Don't have to do anything as this is only called if(shouldVerifyTransactions())
        return null;
    }

    @Override
    protected TransactionOutputChanges connectTransactions(StoredBlock newBlock) {
        // Don't have to do anything as this is only called if(shouldVerifyTransactions())
        return null;
    }

    @Override
    protected void disconnectTransactions(StoredBlock block) {
        // Don't have to do anything as this is only called if(shouldVerifyTransactions())        
    }

    @Override
    protected void preSetChainHead() {
        // We don't use DB transactions here, so we don't need to do anything
    }

    @Override
    protected void notSettingChainHead() throws BlockStoreException {
        // We don't use DB transactions here, so we don't need to do anything
    }
}
