package cz.forgottenempire.servermanager.serverinstance.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "arma3_network_settings")
public class Arma3NetworkSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "max_msg_send")
    private Integer maxMessagesSend;

    @Column(name = "max_size_guaranteed")
    private Integer maxSizeGuaranteed;

    @Column(name = "max_size_nonguaranteed")
    private Integer maxSizeNonguaranteed;

    @Column(name = "min_bandwidth")
    private Integer minBandwidth;

    @Column(name = "max_bandwidth")
    private Integer maxBandwidth;

    @Column(name = "min_error_to_send")
    private Double minErrorToSend;

    @Column(name = "min_error_to_send_near")
    private Double minErrorToSendNear;

    @Column(name = "max_packet_size")
    private Integer maxPacketSize;

    @Column(name = "max_custom_file_size")
    private Integer maxCustomFileSize;
}
