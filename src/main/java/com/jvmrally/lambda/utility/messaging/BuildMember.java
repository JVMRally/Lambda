package com.jvmrally.lambda.utility.messaging;

import net.dv8tion.jda.api.entities.Member;

/**
 * BuildMember
 */
public interface BuildMember {

    BuildMessage to(Member member);
}
