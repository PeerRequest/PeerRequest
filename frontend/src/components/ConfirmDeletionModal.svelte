<script>
    import {
        Modal,
        CloseButton,
        Button

    } from "flowbite-svelte" ;

    export let show = false;
    export let to_delete;
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    }

    function deleteObject() {
        //TODO
    }
</script>

<Modal bind:open={show} on:hide={() => hide ? hide() : null} size="sm" permanent={true}>
    <div class="text-2xl font-extrabold text-gray-900 text-center">
        Confirm Deletion of
        <br>
        {to_delete.name}
    </div>
    <CloseButton class="absolute top-0 right-2"
                 on:click={hide}/>
    <div class="text-center font-bold">
        Are you sure?
    </div>
    <div class="justify-center gap-x-16 w-full flex">
        <Button class="mb-4 h-8" color="primary" on:click={deleteObject()} size="lg">Yes</Button>
        <Button class="mb-4 h-8" color="red" on:click={hide()} size="lg">No</Button>
    </div>

</Modal>