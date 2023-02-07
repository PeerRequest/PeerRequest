<script>
    import {
        Modal,
        Button,
        CloseButton,
        Heading

    } from "flowbite-svelte" ;

    export let show = false;
    export let conference = null;
    export let bidding = null;
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    }
</script>

<Modal bind:open={show} on:hide={() => hide ? hide() : null} permanent={true} size="lg">

    <svelte:fragment slot="header">
        <div class="text-4xl font-extrabold text-gray-900">
            Edit {bidding !== null ? "Bidding" : (conference !== null ? "Conference" : "")}
        </div>
        <CloseButton class="absolute top-3 right-5"
                     on:click={hide}/>
    </svelte:fragment>


    {#if conference !== null}
        <form class="grid gap-y-6">
            <div class="flex flex-row justify-between items-center">
                <Heading size="md" tag="h4"> Name</Heading>
                <input class="min-w-[13.5rem] w-full rounded-lg" id=conference_name type=text value={conference.name}
                       size="{conference.name.length}"
                       onkeypress="this.style.width = ((this.value.length + 8) * 8) + 'px';" required>
            </div>
            <div class="flex flex-row justify-between items-center">
                <Heading size="md" tag="h4"> Year</Heading>
                <input class="w-full rounded-lg" id=conference_year type=number value={conference.year} required>
            </div>

            <div class="flex flex-row justify-between items-center">
                <Heading size="md" tag="h4"> Deadline</Heading>
                <input class="w-full rounded-lg" id=conference_deadline type=date value={conference.deadline} required>
            </div>
            <Button type="submit" color="primary" size="xs">
                Save
            </Button>
        </form>
    {:else}
        {#if bidding !== null}
            <form class="grid gap-y-6">
                <div class="flex flex-row justify-between items-center">
                    <Heading size="md" tag="h4"> Deadline</Heading>
                    <input class="w-full rounded-lg" type=date value={bidding.deadline} required>
                </div>
            </form>
        {/if}
    {/if}

</Modal>