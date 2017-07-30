/*
 * Copyright (c) 2009-2017, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Efficient Java Matrix Library (EJML).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ejml.sparse.csc.decomposition.lu;

import org.ejml.EjmlUnitTests;
import org.ejml.UtilEjml;
import org.ejml.data.DMatrixSparseCSC;
import org.ejml.interfaces.decomposition.LUDecomposition_F64;
import org.ejml.sparse.FillReducing;
import org.ejml.sparse.csc.CommonOps_DSCC;
import org.ejml.sparse.csc.RandomMatrices_DSCC;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author Peter Abeles
 */
public abstract class GenericLuTests_DSCC {
    Random rand = new Random(0x45478);

    protected FillReducing permTests[] =
            new FillReducing[]{FillReducing.NONE, FillReducing.IDENTITY};

    public abstract LUDecomposition_F64<DMatrixSparseCSC> create( FillReducing permutation );

    @Test
    public void checkHandConstructed() {
        for( FillReducing p : permTests) {
            checkHandConstructed(p);
        }
    }

    public void checkHandConstructed(FillReducing perm) {
        DMatrixSparseCSC A = UtilEjml.parse_DSCC(
                     "1 2  4 " +
                        "2 13 23 "+
                        "4 23 90",3);


        LUDecomposition_F64<DMatrixSparseCSC> lu = create(perm);

        checkSolution(A, lu);
    }

    private void checkSolution(DMatrixSparseCSC A, LUDecomposition_F64<DMatrixSparseCSC> lu) {
        System.out.println("00000000000000=-------------------------------------------");

        DMatrixSparseCSC Acpy = A.copy();
        assertTrue(lu.decompose(A));
        assertFalse(lu.isSingular());

        if( !lu.inputModified() ) {
            EjmlUnitTests.assertEquals(A,Acpy, UtilEjml.TEST_F64);
        }

        DMatrixSparseCSC L = lu.getLower(null);
        DMatrixSparseCSC U= lu.getUpper(null);
        DMatrixSparseCSC P = lu.getPivot(null);

        DMatrixSparseCSC PL = new DMatrixSparseCSC(P.numRows,L.numCols,0);
        CommonOps_DSCC.multTransA(P,L,PL,null,null);
        DMatrixSparseCSC found = new DMatrixSparseCSC(PL.numCols,U.numCols,0);
        CommonOps_DSCC.mult(PL,U,found);

        P.print();
//        L.print();
//        U.print();
//        Acpy.print();
//        found.print();
        EjmlUnitTests.assertEquals(Acpy,found,UtilEjml.TEST_F64);
    }

    @Test
    public void checkMontiCarlo() {
        for( FillReducing p : permTests) {
            checkMontiCarlo(p);
        }
    }

    public void checkMontiCarlo( FillReducing perm ) {

        LUDecomposition_F64<DMatrixSparseCSC> lu = create(perm);

        for (int width = 1; width <= 10; width++) {
            for (int mc = 0; mc < 30; mc++) {
                int nz = (int)(width*width*(rand.nextDouble()*0.5+0.02));
                DMatrixSparseCSC A = RandomMatrices_DSCC.rectangle(width,width,nz,rand);
                RandomMatrices_DSCC.ensureNotSingular(A,rand);
                checkSolution(A, lu);
            }
        }
    }


    @Test
    public void testSingular() {
        fail("implement");
    }

    @Test
    public void getL_U_P() {
        fail("implement");
    }

    @Test
    public void checkDeterminant() {
        fail("implement");
    }


}
