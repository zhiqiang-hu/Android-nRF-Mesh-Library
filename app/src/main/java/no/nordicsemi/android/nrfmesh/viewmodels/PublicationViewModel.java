/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.nrfmesh.viewmodels;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import no.nordicsemi.android.mesh.transport.ConfigModelPublicationSet;
import no.nordicsemi.android.mesh.transport.ConfigModelPublicationVirtualAddressSet;
import no.nordicsemi.android.mesh.transport.Element;
import no.nordicsemi.android.mesh.transport.MeshMessage;
import no.nordicsemi.android.mesh.transport.MeshModel;
import no.nordicsemi.android.mesh.transport.ProvisionedMeshNode;
import no.nordicsemi.android.mesh.transport.PublicationSettings;
import no.nordicsemi.android.mesh.utils.AddressType;
import no.nordicsemi.android.mesh.utils.MeshAddress;
import no.nordicsemi.android.mesh.utils.MeshParserUtils;
import no.nordicsemi.android.nrfmesh.R;
import no.nordicsemi.android.nrfmesh.node.PublicationSettingsActivity;

import static no.nordicsemi.android.mesh.transport.PublicationSettings.parseRetransmitIntervalSteps;
import static no.nordicsemi.android.mesh.utils.MeshParserUtils.RESOLUTION_100_MS;
import static no.nordicsemi.android.mesh.utils.MeshParserUtils.RESOLUTION_10_M;
import static no.nordicsemi.android.mesh.utils.MeshParserUtils.RESOLUTION_10_S;
import static no.nordicsemi.android.mesh.utils.MeshParserUtils.RESOLUTION_1_S;

/**
 * View Model class for {@link PublicationSettingsActivity}
 */
public class PublicationViewModel extends BaseViewModel {

    private static final int MIN_PUBLICATION_INTERVAL = 0;
    private static final int MAX_PUBLICATION_INTERVAL = 234;
    private static final int DEFAULT_PUB_RETRANSMIT_COUNT = 1;
    private static final int DEFAULT_PUB_RETRANSMIT_INTERVAL_STEPS = 1;
    private static final int DEFAULT_PUBLICATION_STEPS = 0;
    @SuppressWarnings("unused")
    private static final int DEFAULT_PUBLICATION_RESOLUTION = MeshParserUtils.RESOLUTION_100_MS;

    private UUID labelUUID;
    private int publishAddress;
    private Integer appKeyIndex;
    private int publishTtl = MeshParserUtils.USE_DEFAULT_TTL;
    private int publicationSteps = DEFAULT_PUBLICATION_STEPS;
    private int publicationResolution;
    private int retransmitCount = DEFAULT_PUB_RETRANSMIT_COUNT;
    private int retransmitIntervalSteps = DEFAULT_PUB_RETRANSMIT_INTERVAL_STEPS;
    private boolean credentialsFlag;


    private int lastValue = 0;

    @Inject
    PublicationViewModel(@NonNull final NrfMeshRepository nrfMeshRepository) {
        super(nrfMeshRepository);
    }

    public UUID getLabelUUID() {
        return labelUUID;
    }

    public void setLabelUUID(final UUID labelUUID) {
        this.labelUUID = labelUUID;
    }

    public int getPublishAddress() {
        return publishAddress;
    }

    public void setPublishAddress(final int publishAddress) {
        this.publishAddress = publishAddress;
    }

    public Integer getAppKeyIndex() {
        return appKeyIndex;
    }

    public void setAppKeyIndex(final Integer appKeyIndex) {
        this.appKeyIndex = appKeyIndex;
    }

    public boolean getCredentialsFlag() {
        return credentialsFlag;
    }

    public void setCredentialsFlag(final boolean credentialsFlag) {
        this.credentialsFlag = credentialsFlag;
    }

    public int getPublishTtl() {
        return publishTtl;
    }

    public void setPublishTtl(final int publishTtl) {
        this.publishTtl = publishTtl;
    }

    public int getPublicationSteps() {
        return publicationSteps;
    }

    public void setPublicationSteps(final int publicationSteps) {
        this.publicationSteps = publicationSteps;
    }

    public int getPublicationResolution() {
        return publicationResolution;
    }

    public void setPublicationResolution(final int publicationResolution) {
        this.publicationResolution = publicationResolution;
    }

    public int getRetransmitCount() {
        return retransmitCount;
    }

    public void setRetransmitCount(final int retransmitCount) {
        this.retransmitCount = retransmitCount;
    }

    public int getRetransmitIntervalSteps() {
        return retransmitIntervalSteps;
    }

    public void setRetransmitIntervalSteps(final int retransmitIntervalSteps) {
        this.retransmitIntervalSteps = parseRetransmitIntervalSteps(retransmitIntervalSteps);
    }

    /**
     * Calculates the publication period
     */
    public int getPublishPeriod() {
        return PublicationSettings.getPublishPeriod(publicationResolution, publicationSteps);
    }

    public int getRetransmissionInterval() {
        return PublicationSettings.getRetransmissionInterval(retransmitIntervalSteps);
    }

    /**
     * Initialises the publication settings properties int he view model based on the publication settings.
     *
     * @param publicationSettings Publication settings.
     * @param boundAppKeyIndexes  Bound application key indexes.
     */
    public void setPublicationValues(@Nullable PublicationSettings publicationSettings,
                                     @NonNull List<Integer> boundAppKeyIndexes) {
        if (publicationSettings != null) {
            publishAddress = publicationSettings.getPublishAddress();
            labelUUID = publicationSettings.getLabelUUID();

            credentialsFlag = publicationSettings.getCredentialFlag();
            publishTtl = publicationSettings.getPublishTtl();

            publicationSteps = publicationSettings.getPublicationSteps();
            publicationResolution = publicationSettings.getPublicationResolution();

            retransmitCount = publicationSettings.getPublishRetransmitCount();
            retransmitIntervalSteps = publicationSettings.getPublishRetransmitIntervalSteps();

            //Default app key index to the 0th key in the list of bound app keys
            if (!boundAppKeyIndexes.isEmpty()) {
                appKeyIndex = publicationSettings.getAppKeyIndex();
            }
        }
    }

    public int getPublicationPeriodResolutionResource(final int progress) {
        final int resolutionResource;
        if (progress >= 1 && progress <= 63) {
            lastValue = progress;
            publicationSteps = progress;
            publicationResolution = RESOLUTION_100_MS;
            resolutionResource = R.string.time_ms;
        } else if (progress >= 64 && progress <= 120) {
            if (progress > lastValue) {
                publicationSteps = progress - 57;
                lastValue = progress;
            } else if (progress < lastValue) {
                publicationSteps = -(57 - progress);
            }
            publicationResolution = RESOLUTION_1_S;
            resolutionResource = R.string.time_s;
        } else if (progress >= 121 && progress <= 177) {
            if (progress > lastValue) {
                publicationSteps = (progress - 114);
                lastValue = progress;
            } else if (progress < lastValue) {
                publicationSteps = (-(114 - progress));
            }
            publicationResolution = RESOLUTION_10_S;
            resolutionResource = R.string.time_s;
        } else if (progress >= 178 && progress <= 234) {
            if (progress >= lastValue) {
                publicationSteps = (progress - 171);
                lastValue = progress;
            } else {
                publicationSteps = (-(171 - progress));
            }
            publicationResolution = RESOLUTION_10_M;
            resolutionResource = R.string.time_m;
        } else {
            lastValue = progress;
            publicationSteps = (progress);
            publicationResolution = RESOLUTION_100_MS;
            resolutionResource = R.string.disabled;
        }
        return resolutionResource;
    }

    public MeshMessage createMessage() {
        final Element element = mNrfMeshRepository.getSelectedElement().getValue();
        final MeshModel model = mNrfMeshRepository.getSelectedModel().getValue();
        if (element != null && model != null) {
            final AddressType type = MeshAddress.getAddressType(publishAddress);
            if (type != null && type != AddressType.VIRTUAL_ADDRESS) {
                return new ConfigModelPublicationSet(element.getElementAddress(),
                        publishAddress,
                        appKeyIndex,
                        credentialsFlag,
                        publishTtl,
                        publicationSteps,
                        publicationResolution,
                        retransmitCount,
                        retransmitIntervalSteps,
                        model.getModelId());
            } else {
                return new ConfigModelPublicationVirtualAddressSet(element.getElementAddress(),
                        labelUUID,
                        appKeyIndex,
                        credentialsFlag,
                        publishTtl,
                        publicationSteps,
                        publicationResolution,
                        retransmitCount,
                        retransmitIntervalSteps,
                        model.getModelId());
            }
        }
        return null;
    }
}
